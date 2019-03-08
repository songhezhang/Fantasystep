package com.fantasystep.systemweaver.item;

import com.fantasystep.annotation.ControlType;
import com.fantasystep.annotation.DomainClass;
import com.fantasystep.annotation.FantasyStep;
import com.fantasystep.annotation.FantasyView;
import com.fantasystep.annotation.SerializationType;
import com.fantasystep.annotation.Storage;
import com.fantasystep.systemweaver.SysWDomain;
import com.fantasystep.systemweaver.annotation.SystemWeaver;
import com.fantasystep.systemweaver.itemenum.ComponentType;
import com.fantasystep.systemweaver.itemenum.SimulinkSWC;
import com.fantasystep.systemweaver.itemenum.StbMAPI;
import com.fantasystep.systemweaver.itemenum.SupervisionStrategy;

@DomainClass(validParents = { Composition.class, MicroControllerCoreVirtualMachine.class }, label = "LABEL_LOGIC_COMPONENT", icon = "logic-component.ico")
public class LogicComponent extends SysWDomain {

	private static final long serialVersionUID = 7850475905632299093L;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.TEXTBOX, label = "LABEL_LONG_NAME")
	private String longName;
	
	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, enumType = ComponentType.class, valueOptions = ComponentType.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_COMPONENT_TYPE")
	private ComponentType componentType;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.TEXTBOX, label = "LABEL_RESPONSIBLE_TEAM")
	private String responsibleTeam;
	
	@SystemWeaver
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.TEXTAREA, label = "LABEL_RESPONSIBLE")
	private String responsible;
	
	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, enumType = SimulinkSWC.class, valueOptions = SimulinkSWC.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_SIMULINK_SWC")
	private SimulinkSWC simulinkSWC;
	
	@SystemWeaver
	@FantasyStep(required = true, storage = Storage.MONGO, enumType = StbMAPI.class, valueOptions = StbMAPI.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_STBM_API")
	private StbMAPI stbMAPI;
	
	@SystemWeaver(isPartAttr = true)
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, label = "LABEL_CHECKPOINT_ID")
	private Integer checkpointId;
	
	@SystemWeaver(isPartAttr = true)
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, label = "LABEL_CYCLE_TIME")
	private Integer cycleTimems;

	@SystemWeaver(isPartAttr = true)
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, label = "LABEL_PARTITION")
	private Integer partition;
	
	@SystemWeaver(isPartAttr = true)
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, label = "LABEL_POSITION_IN_TASK")
	private Integer positionInTask;
	
	@SystemWeaver(isPartAttr = true)
	@FantasyStep(storage = Storage.MONGO, enumType = SupervisionStrategy.class, valueOptions = SupervisionStrategy.class, serializationMaximumLength = 32, serializationType = SerializationType.STRING)
	@FantasyView(controlType = ControlType.DROPDOWN, label = "LABEL_SUPERVISION_STRATEGY")
	private SupervisionStrategy supervisionStrategy;
	
	@SystemWeaver(isPartAttr = true)
	@FantasyStep(storage = Storage.MONGO, serializationType = SerializationType.INTEGER)
	@FantasyView(controlType = ControlType.TEXTBOX, label = "LABEL_TASK")
	private Integer task;
	
	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public ComponentType getComponentType() {
		return componentType;
	}

	public void setComponentType(ComponentType componentType) {
		this.componentType = componentType;
	}

	public String getResponsibleTeam() {
		return responsibleTeam;
	}

	public void setResponsibleTeam(String responsibleTeam) {
		this.responsibleTeam = responsibleTeam;
	}

	public SimulinkSWC getSimulinkSWC() {
		return simulinkSWC;
	}

	public void setSimulinkSWC(SimulinkSWC simulinkSWC) {
		this.simulinkSWC = simulinkSWC;
	}

	public StbMAPI getStbMAPI() {
		return stbMAPI;
	}

	public void setStbMAPI(StbMAPI stbMAPI) {
		this.stbMAPI = stbMAPI;
	}

	public Integer getCheckpointId() {
		return checkpointId;
	}

	public void setCheckpointId(Integer checkpointId) {
		this.checkpointId = checkpointId;
	}

	public Integer getCycleTimems() {
		return cycleTimems;
	}

	public void setCycleTimems(Integer cycleTimems) {
		this.cycleTimems = cycleTimems;
	}

	public Integer getPartition() {
		return partition;
	}

	public void setPartition(Integer partition) {
		this.partition = partition;
	}

	public Integer getPositionInTask() {
		return positionInTask;
	}

	public void setPositionInTask(Integer positionInTask) {
		this.positionInTask = positionInTask;
	}

	public SupervisionStrategy getSupervisionStrategy() {
		return supervisionStrategy;
	}

	public void setSupervisionStrategy(SupervisionStrategy supervisionStrategy) {
		this.supervisionStrategy = supervisionStrategy;
	}

	public Integer getTask() {
		return task;
	}

	public void setTask(Integer task) {
		this.task = task;
	}

	public String getResponsible() {
		return responsible;
	}

	public void setResponsible(String responsible) {
		this.responsible = responsible;
	}
}
