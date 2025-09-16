package simu.model;

import simu.framework.IEventType;

/**
 * Event types are defined by the requirements of the simulation model
 *
 * TODO: This must be adapted to the actual simulator
 */
public enum EventType implements IEventType {
    ARR1,
    CHECK_IN_Q,
    SECURITY_Q,
    PASSPORT_CONTROL_Q,
    GATE_Q,
    CHECK_IN,
    SECURITY,
    PASSPORT_CONTROL,
    GATE,
    LUGGAGE_DROP,

    SECURITY_Q_PRIORITY,
    PASSPORT_CONTROL_Q_PRIORITY,
    GATE_Q_PRIORITY,
    SECURITY_PRIORITY,
    PASSPORT_CONTROL_PRIORITY,
    GATE_PRIORITY,
}
