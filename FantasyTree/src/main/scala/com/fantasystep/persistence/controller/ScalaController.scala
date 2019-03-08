package com.fantasystep.persistence.controller

import java.util.Date
import java.util.Map
import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import com.fantasystep.domain.Node
import com.fantasystep.utils.JCompiler
import com.fantasystep.utils.JSON2NodeUtil
import com.fantasystep.utils.NodeClassUtil
import com.fantasystep.persistence.PersistenceProxy
import com.fantasystep.persistence.session.SessionManager
import javax.ejb.EJB
import org.springframework.beans.factory.annotation.Autowired
import javax.servlet.http.HttpServletRequest
import com.fantasystep.persistence.manager.DomainFieldManager
import java.util.ArrayList
import com.fantasystep.utils.JSONUtil
import org.json.JSONObject
import com.fantasystep.persistence.exception.UnauthorizedException
import com.fantasystep.domain.Table
import scala.collection.JavaConversions._
import com.fantasystep.domain.Resource
import com.fantasystep.domain.Table
import org.springframework.web.bind.annotation.RequestMethod
import xml.XML

@Controller
class ScalaController {

  private lazy val logger = LoggerFactory.getLogger(classOf[ScalaController])
  private val compiler: JCompiler = JCompiler.getInstance()
  private val packagePath: String = "com.fantasystep.domain."
  private val SESSION_ID: String = "sessionKey"
  private val RESOURCE_ID: String = "resourceId"

  @EJB
  var persistenceManager: PersistenceProxy = null
  @EJB
  var sessionManager: SessionManager = null
  @Autowired
  var request: HttpServletRequest = null

  private def authorize(tableName: String): Boolean = {
    val sessionKey = Option(request.getHeader(SESSION_ID)).getOrElse(request.getParameter(SESSION_ID))
    val resourceId = Option(request.getHeader(RESOURCE_ID)).getOrElse(request.getParameter(RESOURCE_ID))
    try {
      val resource = persistenceManager.getNodeByID(UUID.fromString(resourceId)).asInstanceOf[Resource]
      val table = resource.getChildren(classOf[Table]).toList
      		.filter(t => t.asInstanceOf[Table].getTableName().endsWith(tableName)).get(0).asInstanceOf[Table]
      
      if(!table.getNeedAuthentication()) return true
      
      val user = sessionManager.getUser(UUID.fromString(sessionKey))
      if(!resource.getMembers().contains(user.getId()))
        throw new UnauthorizedException()
      true
    } catch {
      case e: Exception => 
        e.printStackTrace();
        throw new UnauthorizedException()
    }
  }
  
  private def authorize() = {
    val sessionKey = Option(request.getHeader(SESSION_ID)).getOrElse(request.getParameter(SESSION_ID))
    val user = sessionManager.getUser(UUID.fromString(sessionKey))
    if(user == null)
      throw new UnauthorizedException()
  }
  
  @RequestMapping(value = Array("/rest/auth"), method = Array(RequestMethod.GET))
  @ResponseBody
  def authenticate(@RequestParam("username") username: String, @RequestParam("password") password: String): UUID = {
    persistenceManager.authenticate(username, password)
    val user = persistenceManager.getUserByIdentity( username )
    sessionManager.createSession(user)
  }
  
  @RequestMapping(value = Array("/rest/resources"), method = Array(RequestMethod.GET))
  @ResponseBody
  def getResources(): String = {
    authorize()
    persistenceManager.getResources().toList.map(r => r.getName() + ":" + r.getId()).mkString("\n")
  }

  @RequestMapping(value = Array("/rest/{name}"), method = Array(RequestMethod.GET))
  @ResponseBody
  def getDummyNode(@PathVariable("name") name: String): String = {
    authorize()
    logger.info("Start getNode " + name)
    val clazz = NodeClassUtil.getDynamicEntityClassByFullName(packagePath + name)
    var node: Node = null
    try {
      node = clazz.newInstance()
      clazz.cast(node)
      val id = UUID.randomUUID()
      node.setId(id)
      node.setCreatedDate(new Date())
      JCompiler.printAllProperties(clazz)
    } catch {
      case e: InstantiationException => e.printStackTrace()
      case e: IllegalAccessException => e.printStackTrace()
    }
    JSON2NodeUtil.node2Json(node);
  }

  @RequestMapping(value = Array("/rest/{name}/{id}"), method = Array(RequestMethod.GET))
  @ResponseBody
  def getNode(@PathVariable("name") name: String, @PathVariable("id") nodeId: String, @RequestParam params: Map[String, String]): String = {
    authorize()
    val id = UUID.fromString(nodeId)
    logger.info("Start getNode. ID=" + id)
    val clazz = NodeClassUtil.getDynamicEntityClassByFullName(packagePath + name)
    val map = persistenceManager.getDynamicStorageHandler().read(clazz, id, null)
    JSONUtil.toJSON(map).toString()
  }

  @RequestMapping(value = Array("/rest/all/{name}"), method = Array(RequestMethod.GET))
  @ResponseBody
  def getAllNodes(@PathVariable("name") name: String): String = {
    authorize()
    logger.info("Start getAllNodes.")
    val clazz = NodeClassUtil.getDynamicEntityClassByFullName(packagePath + name)
    val ids: java.util.List[UUID] = new ArrayList[UUID]
    val map = persistenceManager.getDynamicStorageHandler().read(clazz, ids, null)
    new JSONObject(map).toString()
  }

  @RequestMapping(value = Array("/rest/create/{name}"), method = Array(RequestMethod.POST))
  @ResponseBody
  def createNode(@PathVariable("name") name: String, @RequestBody nodeString: String): Node = {
    authorize()
    logger.info("Start createNode." + nodeString)
    val clazz = NodeClassUtil.getDynamicEntityClassByFullName(packagePath + name)
    val node = JSON2NodeUtil.json2Node(nodeString, clazz)
    persistenceManager.getDynamicStorageHandler().insert(clazz, null, DomainFieldManager.getInstance().convertFromDomainToMap(node))
    node
  }

  @RequestMapping(value = Array("/rest/delete/{name}/{id}"), method = Array(RequestMethod.PUT))
  @ResponseBody
  def deleteNode(@PathVariable("name") name: String, @PathVariable("id") nodeId: String): Boolean = {
    authorize()
    logger.info("Start delete node " + nodeId)
    persistenceManager.getDynamicStorageHandler().destroy(NodeClassUtil.getDynamicEntityClassByFullName(packagePath + name), UUID.fromString(nodeId))
  }
  
  @RequestMapping(value = Array("/service/svg/{characters}"), method = Array(RequestMethod.GET))
  @ResponseBody
  def getSvgCharacter(@PathVariable("characters") characters: String, @RequestParam params: Map[String, String]): String = {
    logger.info("Get SVG charecters. ID=" + characters)
    val size = params getOrElse ("size", 24)
    val font = params getOrElse ("font", "serif")
    val width = params getOrElse ("width", "100%")
    val height = params getOrElse ("font", "100%")
    var svg = <svg xmlns:svg="http://www.w3.org/2000/svg" xmlns="http://www.w3.org/2000/svg" width="{width}" height="{height}"><g transform="translate(0, 40.0)" style="font-family: {font};"><text x="0em" y="1em" transform="scale(1, 1)" font-size="{size}"></text><text x="0em" y="1.9em" transform="scale(1, 0.4)" font-size="400">{characters}</text></g></svg>
    val writer = new java.io.StringWriter
    XML.write(writer, svg, "utf-8", xmlDecl = true, doctype = null)
    writer.toString 
  }
}
