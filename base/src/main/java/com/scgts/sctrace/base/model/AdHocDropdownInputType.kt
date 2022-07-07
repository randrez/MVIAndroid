package com.scgts.sctrace.base.model

sealed class AdHocDropdownInputType {
    class TypeProject(val project: Project):AdHocDropdownInputType()
    class DispatchFromRig(val fromRig:Facility):AdHocDropdownInputType()
    class DispatchFromWell(val fromWell:Facility):AdHocDropdownInputType()
    class DispatchToRig(val toRig:Facility):AdHocDropdownInputType()
    class DispatchToWell(val toWell:Facility):AdHocDropdownInputType()
    class DispatchToYard(val toYard:Facility):AdHocDropdownInputType()
    class Yard(val yard:Facility):AdHocDropdownInputType()
    class Location(val location: RackLocation):AdHocDropdownInputType()
    class Rig(val rig:Facility):AdHocDropdownInputType()
    class Well(val well:Facility):AdHocDropdownInputType()
}