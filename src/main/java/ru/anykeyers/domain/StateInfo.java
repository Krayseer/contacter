package ru.anykeyers.domain;

import ru.anykeyers.processor.state.domain.State;
import ru.anykeyers.processor.state.domain.StateType;

/**
 * Информация о состоянии
 */
public class StateInfo {

    /**
     * Состояние<br/>
     * По дефолту значение {@link State#NONE}
     */
    private State state = State.NONE;

    /**
     * Тип состояния<br/>
     * По дефолту значение {@link StateType#NONE}
     */
    private StateType stateType = StateType.NONE;

    /**
     * Информация, для использования при обработке состояния
     */
    private String editInfo;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public StateType getStateType() {
        return stateType;
    }

    public void setStateType(StateType stateType) {
        this.stateType = stateType;
    }

    public String getEditInfo() {
        return editInfo;
    }

    public void setEditInfo(String editInfo) {
        this.editInfo = editInfo;
    }

    /**
     * Очистить всю информацию о состоянии
     */
    public void clear() {
        state = State.NONE;
        stateType = StateType.NONE;
        editInfo = null;
    }

}
