package com.fantasystep.persistence;

import com.fantasystep.systemweaver.item.MicroController;
import com.fantasystep.systemweaver.item.EPlatform;
import com.fantasystep.systemweaver.item.SystemSignalGroup;
import com.fantasystep.systemweaver.item.IntegerSystemSignal;
import com.fantasystep.systemweaver.item.FloatSystemSignal;
import com.fantasystep.domain.Table;
import com.fantasystep.systemweaver.item.DataInMemory;
import com.fantasystep.systemweaver.item.DataTypeEnum;
import com.fantasystep.autosar.AutosarModel;
import com.fantasystep.systemweaver.item.BusController;
import com.fantasystep.systemweaver.item.SomeIpSystemSignal;
import com.fantasystep.systemweaver.item.LogicComponent;
import com.fantasystep.systemweaver.item.BusConnection;
import com.fantasystep.systemweaver.item.DiagnosticEvent;
import com.fantasystep.domain.User;
import com.fantasystep.systemweaver.item.DataIdentifier;
import com.fantasystep.systemweaver.item.Socket;
import com.fantasystep.autosar.AutosarNode;
import com.fantasystep.systemweaver.item.DesignSignalGroup;
import com.fantasystep.systemweaver.item.Frame;
import com.fantasystep.domain.Group;
import com.fantasystep.systemweaver.item.ProtocolDataUnit;
import com.fantasystep.systemweaver.item.Vlan;
import com.fantasystep.systemweaver.item.BooleanDesignSignal;
import com.fantasystep.systemweaver.item.ControlRoutine;
import com.fantasystep.systemweaver.item.IntegerDesignSignal;
import com.fantasystep.systemweaver.SysWModel;
import com.fantasystep.systemweaver.item.SignalMapping;
import com.fantasystep.domain.Entity;
import com.fantasystep.domain.Permission;
import com.fantasystep.systemweaver.item.Composition;
import com.fantasystep.domain.Organization;
import com.fantasystep.domain.EntityGroup;
import com.fantasystep.systemweaver.item.EnumerationSystemSignal;
import com.fantasystep.systemweaver.item.PhysicalLink;
import com.fantasystep.systemweaver.item.ByteArraySystemSignal;
import com.fantasystep.systemweaver.item.ByteArrayDesignSignal;
import com.fantasystep.systemweaver.item.EthernetNetwork;
import com.fantasystep.domain.Resource;
import com.fantasystep.domain.Application;
import com.fantasystep.systemweaver.item.MicroControllerCoreVirtualMachine;
import com.fantasystep.systemweaver.item.EnumerationDesignSignal;
import com.fantasystep.systemweaver.item.EthernetSwitch;
import com.fantasystep.systemweaver.item.RoutineOperation;
import com.fantasystep.systemweaver.item.EcuSw;
import com.fantasystep.systemweaver.item.FloatDesignSignal;

import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.fantasystep.persistence.TreeManager;

@WebService
@XmlSeeAlso({MicroController.class, EPlatform.class, SystemSignalGroup.class, IntegerSystemSignal.class, FloatSystemSignal.class, Table.class, DataInMemory.class, DataTypeEnum.class, AutosarModel.class, BusController.class, SomeIpSystemSignal.class, LogicComponent.class, BusConnection.class, DiagnosticEvent.class, User.class, DataIdentifier.class, Socket.class, AutosarNode.class, DesignSignalGroup.class, Frame.class, Group.class, ProtocolDataUnit.class, Vlan.class, BooleanDesignSignal.class, ControlRoutine.class, IntegerDesignSignal.class, SysWModel.class, SignalMapping.class, Entity.class, Permission.class, Composition.class, Organization.class, EntityGroup.class, EnumerationSystemSignal.class, PhysicalLink.class, ByteArraySystemSignal.class, ByteArrayDesignSignal.class, EthernetNetwork.class, Resource.class, Application.class, MicroControllerCoreVirtualMachine.class, EnumerationDesignSignal.class, EthernetSwitch.class, RoutineOperation.class, EcuSw.class, FloatDesignSignal.class})
public interface TreeManagerSubclassHolder extends TreeManager {

}
