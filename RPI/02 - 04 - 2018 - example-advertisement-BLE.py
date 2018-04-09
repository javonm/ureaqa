#!/usr/bin/python
'''dbus (desktop bus) is an open source communication protocol that allows for concurrent communication
across different software platforms'''

import dbus
import dbus.exceptions #catching errors
import dbus.mainloop.glib #allows dbus to integrate a main loop
import dbus.service #allows exporting stuff across the bus

import array 
import gobject #provides portable object system

from random import randint

#used later
mainloop = None

#setting up the bus parameters
BLUEZ_SERVICE_NAME = 'org.bluez' #dbus connections require a name used in findadapter, main: adapter_props, ad_manager

LE_ADVERTISING_MANAGER_IFACE = 'org.bluez.LEAdvertisingManager1' #used in findadapter and ad_manager
GATT_MANAGER_IFACE = 'org.bluez.GattManager1'

GATT_SERVICE_IFACE = 'org.bluez.GattService1'
GATT_CHRC_IFACE = 'org.bluez.GattCharacteristic1'
GATT_DESC_IFACE = 'org.bluez.GattDescriptor1'

LE_ADVERTISEMENT_IFACE = 'org.bluez.LEAdvertisement1' #used prolifically

DBUS_OM_IFACE = 'org.freedesktop.DBus.ObjectManager' #used in findadapter function
DBUS_PROP_IFACE = 'org.freedesktop.DBus.Properties' #used in service.method


#allows exceptions for common errors using dbus
class InvalidArgsException(dbus.exceptions.DBusException):
    _dbus_error_name = 'org.freedesktop.DBus.Error.InvalidArgs'

class NotSupportedException(dbus.exceptions.DBusException):
    _dbus_error_name = 'org.bluez.Error.NotSupported'

class NotPermittedException(dbus.exceptions.DBusException):
    _dbus_error_name = 'org.bluez.Error.NotPermitted'

class InvalidValueLengthException(dbus.exceptions.DBusException):
    _dbus_error_name = 'org.bluez.Error.InvalidValueLength'

class FailedException(dbus.exceptions.DBusException):
    _dbus_error_name = 'org.bluez.Error.Failed'

#setting up the class to advertise the bluetooth low energy service
class Advertisement(dbus.service.Object):

    #path to the advertisement 
    PATH_BASE = '/org/bluez/example/advertisement'
    
    #attributing the results of the information gathering functions to the advertisement
    def __init__(self, bus, index, advertising_type):
        self.path = self.PATH_BASE + str(index)
        self.bus = bus
        self.ad_type = advertising_type
        self.service_uuids = None
        self.manufacturer_data = None
        self.solicit_uuids = None
        self.service_data = None
        self.local_name = None
        self.include_tx_power = None
        dbus.service.Object.__init__(self, bus, self.path)
   
    #including properties of the device to be advertised by adding to dictionary
    def get_properties(self):
        properties = dict()
        
        #adding service uuids to the array
        properties['Type'] = self.ad_type
        if self.service_uuids is not None:
            properties['ServiceUUIDs'] = dbus.Array(self.service_uuids,
                                                    signature='s')
        if self.solicit_uuids is not None:
            properties['SolicitUUIDs'] = dbus.Array(self.solicit_uuids,
                                                    signature='s')
        #adding the manufacturer data to the dbus dictionary
        if self.manufacturer_data is not None:
            properties['ManufacturerData'] = dbus.Dictionary(
                self.manufacturer_data, signature='qv')
        if self.service_data is not None:
            properties['ServiceData'] = dbus.Dictionary(self.service_data,
                                                        signature='sv')
        #setting the local name 
        if self.local_name is not None:
            properties['LocalName'] = dbus.String(self.local_name)

        #adding power level
        if self.include_tx_power is not None:
            properties['IncludeTxPower'] = dbus.Boolean(self.include_tx_power)

        print'|properties|', properties
        #displaying the settings gathered on the front-facing interface
        return {LE_ADVERTISEMENT_IFACE: properties}

    #obtaining values for parameters previously set to 'None' in def __init_
    def get_path(self):
        return dbus.ObjectPath(self.path)

    def add_service_uuid(self, uuid):
        if not self.service_uuids:
            self.service_uuids = []
        self.service_uuids.append(uuid)

    def add_solicit_uuid(self, uuid):
        if not self.solicit_uuids:
            self.solicit_uuids = []
        self.solicit_uuids.append(uuid)

    def add_manufacturer_data(self, manuf_code, data):
        if not self.manufacturer_data:
            self.manufacturer_data = dbus.Dictionary({}, signature='qv')
        self.manufacturer_data[manuf_code] = dbus.Array(data, signature='y')

    def add_service_data(self, uuid, data):
        if not self.service_data:
            self.service_data = dbus.Dictionary({}, signature='sv')
        self.service_data[uuid] = dbus.Array(data, signature='y')

    def add_local_name(self, name):
        if not self.local_name:
            self.local_name = ""
        self.local_name = dbus.String(name)

    #taking in service and solicit ids (s).
    #sending out service data (sv)
    @dbus.service.method(DBUS_PROP_IFACE,
                         in_signature='s',
                         out_signature='a{sv}')
    
    #interface = LE_ADVERTISEMENT_IFACE, return error if not
    def GetAll(self, interface):
        if interface != LE_ADVERTISEMENT_IFACE:
            raise InvalidArgsException()
                #using the function get_properties to set the LE_ADVERTISEMENT_IFACE values
        return self.get_properties()[LE_ADVERTISEMENT_IFACE]

    @dbus.service.method(LE_ADVERTISEMENT_IFACE,
                         in_signature='',
                         out_signature='')
    
    def Release(self):
        print '%s: Released!' % self.path

#defining the parameters of the BLE attributes and parameters using Advertisement class
class TestAdvertisement(Advertisement):

    def __init__(self, bus, index):
        Advertisement.__init__(self, bus, index, 'peripheral')
        self.add_service_uuid('0x2A57') #digital output
        self.add_service_uuid('0x2A18') #glucose output
        self.add_manufacturer_data(0xaaaa, [0x03, 0x03, 0x03, 0x03, 0x03])
        self.add_service_data('9933', [0x33, 0x33, 0x33, 0x33, 0x33])
        self.add_local_name('TestAdvertisement')
        self.include_tx_power = True


#past the advertisement part 
class Service(dbus.service.Object):
    PATH_BASE = '/org/bluez/example/service'

    def __init__(self, bus, index, uuid, primary):
        self.path = self.PATH_BASE + str(index)
        self.bus = bus
        self.uuid = uuid
        self.primary = primary
        self.characteristics = []
        dbus.service.Object.__init__(self, bus, self.path)

    def get_properties(self):
        return {
            GATT_SERVICE_IFACE: {
                'UUID': self.uuid,
                'Primary': self.primary,
                'Characteristics': dbus.Array(
                    self.get_characteristic_paths(),
                    signature='o')
            }
        }

    def get_path(self):
        return dbus.ObjectPath(self.path)

    def add_characteristic(self, characteristic):
        self.characteristics.append(characteristic)

    def get_characteristic_paths(self):
        result = []
        for chrc in self.characteristics:
            result.append(chrc.get_path())
        return result

    def get_characteristics(self):
        return self.characteristics

    print'|dbus.service.object|', dbus.service.Object
    print'|DBUS_PROP_IFACE|', DBUS_PROP_IFACE
    
    
    @dbus.service.method(DBUS_PROP_IFACE,
                         in_signature='s',
                         out_signature='a{sv}')
    
    
    def GetAll(self, interface):
        if interface != GATT_SERVICE_IFACE:
            raise InvalidArgsException()

        return self.get_properties()[GATT_SERVICE_IFACE]
    
    @dbus.service.method(DBUS_OM_IFACE, out_signature='a{oa{sa{sv}}}') #first difference. not sure why signature is only out and why it is what it is

    #no experience with GetManagedObjects
    def GetManagedObjects(self):
        response = {}  #creating a dictionary called response
        print('GetManagedObjects')
    
        response[self.get_path()] = self.get_properties()
        chrcs = self.get_characteristics()
        
        for chrc in chrcs:  #saving the character properties with their path
            response[chrc.get_path()] = chrc.get_properties()
            descs = chrc.get_descriptors()
            for desc in descs:
                response[desc.get_path()] = desc.get_properties()

        #response is setting the path to the properties
        return response

class Characteristic(dbus.service.Object):
    def __init__(self, bus, index, uuid, flags, service):
        self.path = service.path + '/char' + str(index)
        self.bus = bus
        self.uuid = uuid
        self.service = service
        self.flags = flags
        self.descriptors = []
        dbus.service.Object.__init__(self, bus, self.path)

    def get_properties(self):
        return {
                GATT_CHRC_IFACE: {
                        'Service': self.service.get_path(),
                        'UUID': self.uuid,
                        'Flags': self.flags,
                        'Descriptors': dbus.Array(
                                self.get_descriptor_paths(),
                                signature='o')
                }
        }

    def get_path(self):
        return dbus.ObjectPath(self.path)

    def add_descriptor(self, descriptor):
        self.descriptors.append(descriptor)

    #getting properties of descriptor
    def get_descriptor_paths(self):
        result = []
        for desc in self.descriptors:
            result.append(desc.get_path())
        return result

    def get_descriptors(self):
        return self.descriptors

    @dbus.service.method(DBUS_PROP_IFACE,
                         in_signature='s',
                         out_signature='a{sv}')
    
    def GetAll(self, interface):
        if interface != GATT_CHRC_IFACE:
            raise InvalidArgsException()

        return self.get_properties[GATT_CHRC_IFACE]

    @dbus.service.method(GATT_CHRC_IFACE, out_signature='ay')
    def ReadValue(self):
        print('Default ReadValue called, returning error')
        raise NotSupportedException()

    @dbus.service.method(GATT_CHRC_IFACE, in_signature='ay')
    def WriteValue(self, value):
        print('Default WriteValue called, returning error')
        raise NotSupportedException()

    @dbus.service.method(GATT_CHRC_IFACE)
    def StartNotify(self):
        print('Default StartNotify called, returning error')
        raise NotSupportedException()

    @dbus.service.method(GATT_CHRC_IFACE)
    def StopNotify(self):
        print('Default StopNotify called, returning error')
        raise NotSupportedException()

    @dbus.service.signal(DBUS_PROP_IFACE,
                         signature='sa{sv}as')
    def PropertiesChanged(self, interface, changed, invalidated):
        pass


class Descriptor(dbus.service.Object):
    def __init__(self, bus, index, uuid, flags, characteristic):
        self.path = characteristic.path + '/desc' + str(index)
        self.bus = bus
        self.uuid = uuid
        self.flags = flags
        self.chrc = characteristic
        dbus.service.Object.__init__(self, bus, self.path)

    def get_properties(self):
        return {
            GATT_DESC_IFACE: {
                'Characteristic': self.chrc.get_path(),
                'UUID': self.uuid,
                'Flags': self.flags,
            }
        }

    def get_path(self):
        return dbus.ObjectPath(self.path)

    @dbus.service.method(DBUS_PROP_IFACE,
                         in_signature='s',
                         out_signature='a{sv}')
    def GetAll(self, interface):
        if interface != GATT_DESC_IFACE:
            raise InvalidArgsException()

        return self.get_properties()[GATT_CHRC_IFACE]

    @dbus.service.method(GATT_DESC_IFACE,
                         in_signature='a{sv}',
                         out_signature='ay')
    
    def ReadValue(self, options):
        print('Default ReadValue called, returning error')
        raise NotSupportedException()

    @dbus.service.method(GATT_DESC_IFACE, in_signature='aya{sv}')
    def WriteValue(self, value, options):
        print('Default WriteValue called, returning error')
        raise NotSupportedException()    


#battery service

class BatteryService(Service):
    
    """
    Fake Battery service that emulates a draining battery.
    """
    
    BATTERY_UUID = '180f'

    def __init__(self, bus, index):
        Service.__init__(self, bus, index, self.BATTERY_UUID, True)
        self.add_characteristic(BatteryLevelCharacteristic(bus, 0, self))


class BatteryLevelCharacteristic(Characteristic):
    """
    Fake Battery Level characteristic. The battery level is drained by 2 points
    every 5 seconds.
    """
    BATTERY_LVL_UUID = '2a19'

    def __init__(self, bus, index, service):
        Characteristic.__init__(
                self, bus, index,
                self.BATTERY_LVL_UUID,
                ['write', 'read', 'notify'],
                service)
        self.notifying = False
        self.battery_lvl = 100
        
    def notify_battery_level(self):
        if not self.notifying:
            return
        self.PropertiesChanged(GATT_CHRC_IFACE,
                { 'Value': [dbus.Byte(self.battery_lvl)] }, [])

    def drain_battery(self):
        if self.battery_lvl > 0:
            self.battery_lvl -= 2
            if self.battery_lvl < 0:
                self.battery_lvl = 0
        print('Battery Level drained: ' + repr(self.battery_lvl))
        self.notify_battery_level()
        return True

    def ReadValue(self):
        print('Battery Level read: ' + repr(self.battery_lvl))
        return [dbus.Byte(self.battery_lvl)]

    def StartNotify(self):
        if self.notifying:
            print('Already notifying, nothing to do')
            return

        self.notifying = True
        self.notify_battery_level()

    def StopNotify(self):
        if not self.notifying:
            print('Not notifying, nothing to do')
            return

        self.notifying = False


#|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||


def register_ad_cb():
    print 'Advertisement registered'

def register_gatt_noerror():
    print 'GATT registered'

def register_ad_error_cb(error):
    print 'Failed to register advertisement: ' + str(error)
    mainloop.quit()

def register_gatt_error(error):
    print'Failed to register GATT' + str(error)
    mainloop.quit()

#finding a service using dbus interface
def find_adapter(bus):
    remote_om = dbus.Interface(bus.get_object(BLUEZ_SERVICE_NAME, '/'),
                               DBUS_OM_IFACE)
    #print '|remote_om|', remote_om

    objects = remote_om.GetManagedObjects()
    #print '|objects|', objects

    #if LE_ADVERTISEMENT_IFACE is in iteritems then o is returned
    for o, props in objects.iteritems():
        #print ('|o|', o)
        #print ('|props|', props)
        
        if LE_ADVERTISING_MANAGER_IFACE in props:
            #print('found o')
            return o
    
    return None

#finding a service for the gatt_manager
def find_adapter_gattmanager(bus):
    remote_om = dbus.Interface(bus.get_object(BLUEZ_SERVICE_NAME, '/'),
                               DBUS_OM_IFACE)
    objects = remote_om.GetManagedObjects()

    for o, props in objects.items():
        if GATT_MANAGER_IFACE in props.keys():
            return o

    return None

'''main loop of activities'''

def main():
    global mainloop

    dbus.mainloop.glib.DBusGMainLoop(set_as_default = True)

    #connects to the systems bus. A "system bus" for notifications from the system to user sessions, and to allow the system to request input from user sessions.
    bus = dbus.SystemBus()

    #calling def_adapter with dbus.Interface using bluez_service_name and dbus_om_iface 
    adapter = find_adapter(bus)
    if not adapter:
        print 'whoops, no adapter'
        return

    #using the interface, bus gets the service and adapter as well as the pathway
    adapter_props = dbus.Interface(bus.get_object(BLUEZ_SERVICE_NAME, adapter),
                                   DBUS_PROP_IFACE);
    
    #setting the name and status of the dbus
    adapter_props.Set("org.bluez.Adapter1", "Powered", dbus.Boolean(1))
    
    #preparing to launch the advertisement
    ad_manager = dbus.Interface(bus.get_object(BLUEZ_SERVICE_NAME, adapter),
                                LE_ADVERTISING_MANAGER_IFACE)


    #using the interface to get the bus for the gatt interface
    adapter_gattmanager = find_adapter_gattmanager(bus)
    if not adapter_gattmanager:
        print('GattManager1 interface not found')
        return

    #getting the org.bluez service and adapter: hci0, applying the GATT_MANGER_INTERFACE
    service_manager = dbus.Interface(bus.get_object(BLUEZ_SERVICE_NAME, adapter), GATT_MANAGER_IFACE)
    
    #redefining the class TestAdvertisement to be used
    test_advertisement = TestAdvertisement(bus, 1)
    test_gattmanager = BatteryService(bus, 0)
    
    print'|TestAdvertisement|', TestAdvertisement
    print'|bus|:', bus
    print'|test_advertisement|:', test_advertisement
    print'|ad_manager|:', ad_manager
    print'|adapter|:', adapter
    print'|adapter_props|:', adapter_props

    #looping
    mainloop = gobject.MainLoop()

    #registers the advertisement - allowing discovery
    ad_manager.RegisterAdvertisement(test_advertisement.get_path(), {},
                                     reply_handler=register_ad_cb,
                                     error_handler=register_ad_error_cb)

    service_manager.RegisterApplication(test_gattmanager.get_path(), {},
                                    reply_handler=register_gatt_noerror,
                                    error_handler=register_gatt_error)
    
    #running the loop
    mainloop.run()
    
if __name__ == '__main__':
    main()
