package honeyroasted.cashews.context.impl;

import honeyroasted.cashews.context.CodeLabel;
import honeyroasted.cashews.context.CodeScope;
import honeyroasted.jype.system.JTypeConstants;
import honeyroasted.jype.type.JType;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryCodeScope implements CodeScope {
    private CodeScope parent;

    private Map<String, Var> vars = new LinkedHashMap<>();
    private Map<Control.Kind, Control> controls = new EnumMap<>(Control.Kind.class);

    private int counter = 0;

    public InMemoryCodeScope(CodeScope parent) {
        this.parent = parent;
    }

    @Override
    public CodeScope parent() {
        return this.parent;
    }

    @Override
    public CodeScope createChild() {
        return new InMemoryCodeScope(this);
    }

    @Override
    public Optional<Control> control(Control.Kind type) {
        Control control = this.controls.get(type);
        if (control != null) {
            return Optional.of(control);
        } else if (this.parent != null) {
            return this.parent.control(type);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Control> control(Control.Kind type, String name) {
        if (name == null) return control(type);

        Control control = this.controls.get(type);
        if (control != null && name.equals(control.name())) {
            return Optional.of(control);
        } else if (this.parent != null) {
            return this.parent.control(type, name);
        }

        return Optional.empty();
    }

    @Override
    public Control defineControl(Control.Kind type, String name, CodeLabel label) {
        Control control = new Control(name, label == null ? null : label);
        //TODO add default label
        this.controls.put(type, control);
        return control;
    }

    @Override
    public Optional<Var> var(String name) {
        Var v = this.vars.get(name);
        if (v != null) {
            return Optional.of(v);
        } else if (this.parent != null) {
            return this.parent.var(name);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Var> defineVar(String name, JType type) {
        if (var(name).isPresent() || type.equals(type.typeSystem().constants().voidType())) {
            return Optional.empty();
        } else {
            Var var = new Var(type, nextIndex(type));
            this.vars.put(name, var);
            return Optional.of(var);
        }
    }

    @Override
    public int nextIndex(JType type) {
        if (this.parent != null) return this.parent.nextIndex(type);

        JTypeConstants constants = type.typeSystem().constants();
        int result = this.counter;

        if (type.equals(constants.longType()) || type.equals(constants.doubleType())) {
            this.counter += 2;
        } else if (!type.equals(constants.voidType())) {
            this.counter += 1;
        }

        return result;
    }
}
