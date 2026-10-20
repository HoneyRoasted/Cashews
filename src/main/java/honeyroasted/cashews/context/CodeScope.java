package honeyroasted.cashews.context;

import honeyroasted.jype.type.JType;

import java.util.Optional;

public interface CodeScope {

    CodeScope parent();

    CodeScope createChild();

    Optional<Control> control(Control.Kind type);

    Optional<Control> control(Control.Kind type, String name);

    Control defineControl(Control.Kind type, String name, CodeLabel label);

    default Control defineControl(Control.Kind type, String name) {
        return defineControl(type, name, null);
    }

    Optional<Var> var(String name);

    Optional<Var> defineVar(String name, JType type);

    int nextIndex(JType type);

    record Control(String name, CodeLabel label) {
        public enum Kind { BREAK, CONTINUE }
    }

    record Var(JType type, int index) {}

}
