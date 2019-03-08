package com.fantasystep.systemweaver.itemenum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fantasystep.annotation.ValueOptionEntry;
import com.fantasystep.annotation.ValueOptions;
import com.fantasystep.systemweaver.SysWDomain;
import com.fantasystep.systemweaver.item.BooleanDesignSignal;
import com.fantasystep.systemweaver.item.BusConnection;
import com.fantasystep.systemweaver.item.BusController;
import com.fantasystep.systemweaver.item.ByteArrayDesignSignal;
import com.fantasystep.systemweaver.item.ByteArraySystemSignal;
import com.fantasystep.systemweaver.item.Composition;
import com.fantasystep.systemweaver.item.ControlRoutine;
import com.fantasystep.systemweaver.item.DataIdentifier;
import com.fantasystep.systemweaver.item.DataInMemory;
import com.fantasystep.systemweaver.item.DataTypeEnum;
import com.fantasystep.systemweaver.item.DesignSignalGroup;
import com.fantasystep.systemweaver.item.DiagnosticEvent;
import com.fantasystep.systemweaver.item.EPlatform;
import com.fantasystep.systemweaver.item.EcuSw;
import com.fantasystep.systemweaver.item.EnumerationDesignSignal;
import com.fantasystep.systemweaver.item.EnumerationSystemSignal;
import com.fantasystep.systemweaver.item.EthernetNetwork;
import com.fantasystep.systemweaver.item.EthernetSwitch;
import com.fantasystep.systemweaver.item.FloatDesignSignal;
import com.fantasystep.systemweaver.item.FloatSystemSignal;
import com.fantasystep.systemweaver.item.Frame;
import com.fantasystep.systemweaver.item.IntegerDesignSignal;
import com.fantasystep.systemweaver.item.IntegerSystemSignal;
import com.fantasystep.systemweaver.item.LogicComponent;
import com.fantasystep.systemweaver.item.MicroController;
import com.fantasystep.systemweaver.item.MicroControllerCoreVirtualMachine;
import com.fantasystep.systemweaver.item.PhysicalLink;
import com.fantasystep.systemweaver.item.ProtocolDataUnit;
import com.fantasystep.systemweaver.item.RoutineOperation;
import com.fantasystep.systemweaver.item.SignalMapping;
import com.fantasystep.systemweaver.item.Socket;
import com.fantasystep.systemweaver.item.SomeIpSystemSignal;
import com.fantasystep.systemweaver.item.SystemSignalGroup;
import com.fantasystep.systemweaver.item.Vlan;

public final class VccModelMapping {

    public static enum VCC_ITEM_TYPE {
        ABSTRACT_DESIGN_SIGNAL,
        BOOLEAN_DESIGN_SIGNAL,  BOOLEAN_PARAMETER, BUS_CONNECTION, BUS_CONTROLLER,
        COMPOSITION, CONTROL_ROUTINE,
        DATA_TYPE_ENUM, DEFAULT, DESIGN_SIGNAL_GROUP, DIAGNOSTIC_EVENT,
        E_PLATFORM, ENUMERATION_DESIGN_SIGNAL, ENUMERATION_SYSTEM_SIGNAL, ETHERNET_NETWORK, ETHERNET_SWITCH,
        FIXED_POINT_PARAMETER, FLOAT_DESIGN_SIGNAL, FLOAT_PARAMETER, FLOAT_SYSTEM_SIGNAL, FRAME, FUNCTION_CALL,
        INTEGER_DESIGN_SIGNAL, INTEGER_SYSTEM_SIGNAL,
        LOGIC_COMPONENT,
        MICROCONTROLLER, MICROCONTROLLER_CORE,
        NODE,
        PHYSICAL_LINK,
        ROUTINE_OPERATION,
        SIGNAL_MAPPING, SOCKET, SOME_IP_SYSTEM_SIGNAL, SYSTEM_SIGNAL_GROUP,
        VLAN
    }

	public static enum VCC_PART_TYPE implements ValueOptions {
		ACCESS_NON_VOLATILE_DATA,
		BUS_CONNECTIONS, BUS_CONTROLLER, BUS_IN_PORT, BUS_OUT_PORT,
		CHANNELS, COMMUNICATION_CONFIG, CORES,
		DATATYPE, DEFAULT, DESCRIPTION_REFERENCE, DTC_SET, DTC_GET,
		EXPLICIT_NV_DATA, 
		FRAMES, 
		IN_PORT, IMPLEMENTATION_SIGNAL, IMPLICIT_NV_DATA, IN_PORT_CONTROL_ROUTINE,
		MCUS,
		NODES,
		OUT_PORT,
		PROVIDED_CONTROL_ROUTINE,
		PROVIDED_DID, 
		RECEIVING_PDUS, RECEIVING_CONTROLLER, 
		SENDING_CONTROLLER, SENDING_PDUS, SIGNAL_MAPPINGS, SIMPLE_DECOMP_PART, SOCKETS, SOMEIP_PDUS, SUB_CONNECTOR, SYSTEM_SIGNAL, SYSTEM_SIGNALS, NOT_SET;
		
		private String label;

		private VCC_PART_TYPE() {
			this.label = this.toString();
		}

		public String getLabel() {
			return label;
		}

		private List<ValueOptionEntry> entries;

		public List<ValueOptionEntry> getValues() {
			if (entries == null) {
				entries = new ArrayList<ValueOptionEntry>();
				for (final VCC_PART_TYPE g : VCC_PART_TYPE.values()) {
					entries.add(new ValueOptionEntry() {

						public Object getValue() {
							return g;
						}

						public String getLabel() {
							return g.getLabel();
						}
					});
				}
			}
			return entries;
		}
	}

    public static enum VCC_ATTR_TYPE {
        ALIGNMENT,
        BASE_TYPE, BYTE_ORDER, BUS_TYPE,
        CHECKPOINT_ID, CHECK_END_TO_END_PROTECTIRON, CHECK_UPDATE, COMPONENT_TYPE, CYCLE_TIME, CONTROL_ROUTINE_ID,
        DATATYPE_NAME, DEFAULT, DEFAULT_GATEWAY, DEST_PORT_NUMBER, DESTINATION_IP_ADDRESS, DID_SIZE, DIMENSION, DTC,
        FIXED_POINT_TYPE, FLOAT_TYPE,
        HOST_LOG,
        INITIAL_VALUE, INTERFACE_VERSION, IP_ADDRESS, IS_SERVICE,
        MAX, MESSAGE_ID, MESSAGE_TYPE, MIN,
        NETMASK, NUMBER_OF_DATA_SETS,NUMBER_OF_ELEMENTS,
        OPERATING_SYSTEM,
        PORT_NUMBER, PROTECT_END_TO_END, PROTOCOL,
        ROUTING_SCHEME, REQUEST_SIZE, RESPONSE_SIZE,
        SCALING_FACTOR, SCALING_OFFSET, SDB_TYPE, SERIALIZATION, SOURCE_PORT_NUMBER, STBM_API, SUPERVISION_STRATEGY, 
        VLAN_ID,
        SIMULINK_SWC
    }

	public static final HashMap<String, Class<? extends SysWDomain>> itemSsidToClassMap = new HashMap<String, Class<? extends SysWDomain>>();

    static {
//        itemSsidToClassMap.put("2DSA", VCC_ITEM_TYPE.ABSTRACT_DESIGN_SIGNAL);
    	itemSsidToClassMap.put("2DSC", BooleanDesignSignal.class);
//        itemSsidToClassMap.put("2P05", VCC_ITEM_TYPE.BOOLEAN_PARAMETER);
        itemSsidToClassMap.put("2BCON", BusConnection.class);
        itemSsidToClassMap.put("2BUSC", BusController.class);
        itemSsidToClassMap.put("SVLC", Composition.class);
        itemSsidToClassMap.put("2DTEN", DataTypeEnum.class);
//        itemSsidToClassMap.put("", VCC_ITEM_TYPE.DEFAULT);
        itemSsidToClassMap.put("2DSH", DesignSignalGroup.class);
        itemSsidToClassMap.put("2DTC", DiagnosticEvent.class);
        itemSsidToClassMap.put("2DSF", EnumerationDesignSignal.class);
        itemSsidToClassMap.put("2SYSE", EnumerationSystemSignal.class);
        itemSsidToClassMap.put("JVEH", EPlatform.class);
        itemSsidToClassMap.put("2NETW", EthernetNetwork.class);
        itemSsidToClassMap.put("2SWITCH", EthernetSwitch.class);
//        itemSsidToClassMap.put("2P18", VCC_ITEM_TYPE.FIXED_POINT_PARAMETER);
        itemSsidToClassMap.put("2DSM", FloatDesignSignal.class);
//        itemSsidToClassMap.put("2P07", VCC_ITEM_TYPE.FLOAT_PARAMETER);
        itemSsidToClassMap.put("2SYSF", FloatSystemSignal.class);
        itemSsidToClassMap.put("2FRAME", Frame.class);
//        itemSsidToClassMap.put("CFC", VCC_ITEM_TYPE.FUNCTION_CALL);
        itemSsidToClassMap.put("2DSX", IntegerDesignSignal.class);
        itemSsidToClassMap.put("2SYSI", IntegerSystemSignal.class);
        itemSsidToClassMap.put("SLC", LogicComponent.class);
        itemSsidToClassMap.put("2MCU", MicroController.class);
        itemSsidToClassMap.put("2CORE", MicroControllerCoreVirtualMachine.class);
        itemSsidToClassMap.put("SSN", EcuSw.class);
        itemSsidToClassMap.put("2SWIPO", PhysicalLink.class);
        itemSsidToClassMap.put("2SSMAP", SignalMapping.class);
        itemSsidToClassMap.put("2TPRO", Socket.class);
        itemSsidToClassMap.put("2SYSO", SomeIpSystemSignal.class);
        itemSsidToClassMap.put("2SYSG", SystemSignalGroup.class);
        itemSsidToClassMap.put("2CHANNEL", Vlan.class);
        itemSsidToClassMap.put("2RID", ControlRoutine.class);
        
        itemSsidToClassMap.put("2DID", DataIdentifier.class);
        itemSsidToClassMap.put("2NVD", DataInMemory.class);
        itemSsidToClassMap.put("2ROPER", RoutineOperation.class);
        itemSsidToClassMap.put("2DBA", ByteArrayDesignSignal.class);
        itemSsidToClassMap.put("2SYSB", ByteArraySystemSignal.class);
        itemSsidToClassMap.put("IPDU", ProtocolDataUnit.class);
        
    }

	public static final HashMap<String, VccModelMapping.VCC_PART_TYPE> partSsidToEnumMap = new HashMap<String, VccModelMapping.VCC_PART_TYPE>();

	static {
		partSsidToEnumMap.put("2ANV", VCC_PART_TYPE.ACCESS_NON_VOLATILE_DATA);
		partSsidToEnumMap.put("2PBCON", VCC_PART_TYPE.BUS_CONNECTIONS);
		partSsidToEnumMap.put("2PETH", VCC_PART_TYPE.BUS_CONTROLLER);
		partSsidToEnumMap.put("2IBIS", VCC_PART_TYPE.BUS_IN_PORT);
		partSsidToEnumMap.put("2IBOS", VCC_PART_TYPE.BUS_OUT_PORT);
		partSsidToEnumMap.put("2PCHA", VCC_PART_TYPE.CHANNELS);
		partSsidToEnumMap.put("2PCOM", VCC_PART_TYPE.COMMUNICATION_CONFIG);
		partSsidToEnumMap.put("2PCORE", VCC_PART_TYPE.CORES);
		partSsidToEnumMap.put("2PDTE", VCC_PART_TYPE.DATATYPE);
		partSsidToEnumMap.put("", VCC_PART_TYPE.DEFAULT);
		partSsidToEnumMap.put("IDR", VCC_PART_TYPE.DESCRIPTION_REFERENCE);
		partSsidToEnumMap.put("2STD", VCC_PART_TYPE.DTC_SET);
		partSsidToEnumMap.put("2GDT", VCC_PART_TYPE.DTC_GET);
		partSsidToEnumMap.put("2NVE", VCC_PART_TYPE.EXPLICIT_NV_DATA);
		partSsidToEnumMap.put("2PFR", VCC_PART_TYPE.FRAMES);
		partSsidToEnumMap.put("IBIS", VCC_PART_TYPE.IN_PORT);
		partSsidToEnumMap.put("2IIS", VCC_PART_TYPE.IN_PORT_CONTROL_ROUTINE);
		partSsidToEnumMap.put("2NVI", VCC_PART_TYPE.IMPLICIT_NV_DATA);
		partSsidToEnumMap.put("2PIMS", VCC_PART_TYPE.IMPLEMENTATION_SIGNAL);
		partSsidToEnumMap.put("2PMCU", VCC_PART_TYPE.MCUS);
		partSsidToEnumMap.put("IVEN", VCC_PART_TYPE.NODES);
		partSsidToEnumMap.put("IBOS", VCC_PART_TYPE.OUT_PORT);
		partSsidToEnumMap.put("2PDI", VCC_PART_TYPE.PROVIDED_DID);
		partSsidToEnumMap.put("2PCR", VCC_PART_TYPE.PROVIDED_CONTROL_ROUTINE);
		partSsidToEnumMap.put("6RPDU", VCC_PART_TYPE.RECEIVING_PDUS);
		partSsidToEnumMap.put("2RLINK", VCC_PART_TYPE.RECEIVING_CONTROLLER);
		partSsidToEnumMap.put("2PLINK", VCC_PART_TYPE.SENDING_CONTROLLER);
		partSsidToEnumMap.put("6SPDU", VCC_PART_TYPE.SENDING_PDUS);
		partSsidToEnumMap.put("2MAPS", VCC_PART_TYPE.SIGNAL_MAPPINGS);
		partSsidToEnumMap.put("IDS", VCC_PART_TYPE.SIMPLE_DECOMP_PART);
		partSsidToEnumMap.put("2PTP", VCC_PART_TYPE.SOCKETS);
		partSsidToEnumMap.put("2PSPDU", VCC_PART_TYPE.SOMEIP_PDUS);
		partSsidToEnumMap.put("ICDP", VCC_PART_TYPE.SUB_CONNECTOR);
		partSsidToEnumMap.put("2PIS", VCC_PART_TYPE.SYSTEM_SIGNAL);
		partSsidToEnumMap.put("2SYSS", VCC_PART_TYPE.SYSTEM_SIGNALS);
		
	}

	public static final HashMap<String, VccModelMapping.VCC_ATTR_TYPE> attrSsidToEnumMap = new HashMap<String, VccModelMapping.VCC_ATTR_TYPE>();

    static {
        attrSsidToEnumMap.put("4ALIG", VCC_ATTR_TYPE.ALIGNMENT);
        attrSsidToEnumMap.put("2AP8", VCC_ATTR_TYPE.BASE_TYPE);
        attrSsidToEnumMap.put("4BUST", VCC_ATTR_TYPE.BUS_TYPE);
        attrSsidToEnumMap.put("4BYTO", VCC_ATTR_TYPE.BYTE_ORDER);
        attrSsidToEnumMap.put("2CCI", VCC_ATTR_TYPE.CHECKPOINT_ID);
        attrSsidToEnumMap.put("2CEE", VCC_ATTR_TYPE.CHECK_END_TO_END_PROTECTIRON);
        attrSsidToEnumMap.put("2CUB", VCC_ATTR_TYPE.CHECK_UPDATE);
        attrSsidToEnumMap.put("2ACT", VCC_ATTR_TYPE.COMPONENT_TYPE);
        attrSsidToEnumMap.put("2CTM", VCC_ATTR_TYPE.CYCLE_TIME);
        attrSsidToEnumMap.put("2DTN", VCC_ATTR_TYPE.DATATYPE_NAME);
        attrSsidToEnumMap.put("", VCC_ATTR_TYPE.DEFAULT);
        attrSsidToEnumMap.put("6DGW", VCC_ATTR_TYPE.DEFAULT_GATEWAY);
        attrSsidToEnumMap.put("DPORT", VCC_ATTR_TYPE.DEST_PORT_NUMBER);
        attrSsidToEnumMap.put("6DIP", VCC_ATTR_TYPE.DESTINATION_IP_ADDRESS);
        attrSsidToEnumMap.put("2CDZ", VCC_ATTR_TYPE.DID_SIZE);
        attrSsidToEnumMap.put("PMUL", VCC_ATTR_TYPE.DIMENSION);
        attrSsidToEnumMap.put("2DTR", VCC_ATTR_TYPE.DTC);
        attrSsidToEnumMap.put("2ABF", VCC_ATTR_TYPE.FIXED_POINT_TYPE);
        attrSsidToEnumMap.put("2ABE", VCC_ATTR_TYPE.FLOAT_TYPE);
        attrSsidToEnumMap.put("2HOSTLOG", VCC_ATTR_TYPE.HOST_LOG);
        attrSsidToEnumMap.put("2IIV", VCC_ATTR_TYPE.INITIAL_VALUE);
        attrSsidToEnumMap.put("2SIPIV", VCC_ATTR_TYPE.INTERFACE_VERSION);
        attrSsidToEnumMap.put("6IP", VCC_ATTR_TYPE.IP_ADDRESS);
        attrSsidToEnumMap.put("ARIS", VCC_ATTR_TYPE.IS_SERVICE);
        attrSsidToEnumMap.put("2AP5", VCC_ATTR_TYPE.MAX);
        attrSsidToEnumMap.put("4MEID", VCC_ATTR_TYPE.MESSAGE_ID);
        attrSsidToEnumMap.put("4MEST", VCC_ATTR_TYPE.MESSAGE_TYPE);
        attrSsidToEnumMap.put("2AP6", VCC_ATTR_TYPE.MIN);
        attrSsidToEnumMap.put("6NETM", VCC_ATTR_TYPE.NETMASK);
        attrSsidToEnumMap.put("2NVS", VCC_ATTR_TYPE.NUMBER_OF_DATA_SETS);
        attrSsidToEnumMap.put("2NVE", VCC_ATTR_TYPE.NUMBER_OF_ELEMENTS);
        attrSsidToEnumMap.put("2OS", VCC_ATTR_TYPE.OPERATING_SYSTEM);
        attrSsidToEnumMap.put("PORT", VCC_ATTR_TYPE.PORT_NUMBER);
        attrSsidToEnumMap.put("2CPE", VCC_ATTR_TYPE.PROTECT_END_TO_END);
        attrSsidToEnumMap.put("PROT", VCC_ATTR_TYPE.PROTOCOL);
        attrSsidToEnumMap.put("6RS", VCC_ATTR_TYPE.ROUTING_SCHEME);
        attrSsidToEnumMap.put("2SCM", VCC_ATTR_TYPE.SCALING_FACTOR);
        attrSsidToEnumMap.put("2IOF", VCC_ATTR_TYPE.SCALING_OFFSET);
        attrSsidToEnumMap.put("2SDB", VCC_ATTR_TYPE.SDB_TYPE);
        attrSsidToEnumMap.put("2SER", VCC_ATTR_TYPE.SERIALIZATION);
        attrSsidToEnumMap.put("SPORT", VCC_ATTR_TYPE.SOURCE_PORT_NUMBER);
        attrSsidToEnumMap.put("4STBM", VCC_ATTR_TYPE.STBM_API);
        attrSsidToEnumMap.put("2CSU", VCC_ATTR_TYPE.SUPERVISION_STRATEGY);
        attrSsidToEnumMap.put("4VLANID", VCC_ATTR_TYPE.VLAN_ID);
        attrSsidToEnumMap.put("4SSWC", VCC_ATTR_TYPE.SIMULINK_SWC);
        attrSsidToEnumMap.put("2AID", VCC_ATTR_TYPE.CONTROL_ROUTINE_ID);
        attrSsidToEnumMap.put("2RESI", VCC_ATTR_TYPE.REQUEST_SIZE);
        attrSsidToEnumMap.put("2RESPSI", VCC_ATTR_TYPE.RESPONSE_SIZE);
    }
}
