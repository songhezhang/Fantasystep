package com.fantasystep.systemweaver.extension

import com.fantasystep.systemweaver.item.EPlatform
import com.fantasystep.systemweaver.item.EcuSw
import com.fantasystep.systemweaver.item.EthernetNetwork
import com.fantasystep.systemweaver.item.MicroController

object Main {
  def main(args: Array[String]): Unit = {
    val platform = new EPlatform
    val ecu = new EcuSw
    val microController = new MicroController
    val ethernetNetwork = new EthernetNetwork
    platform.addChild(ecu)
    ecu.addChild(microController)
    platform.addChild(ethernetNetwork)
    Generic.start(platform)
  }
}