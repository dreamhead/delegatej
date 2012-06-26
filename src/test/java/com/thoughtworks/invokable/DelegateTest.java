package com.thoughtworks.invokable;

import org.junit.Test;

import java.util.List;

import static com.thoughtworks.invokable.Delegate.delegate;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class DelegateTest {
    @Test
    public void should_delegate_annotation_to_interface() {
        List<Invoker<Handle, Executable>> invokers = delegate(Handle.class).to(Executable.class).with(new Runner());
        Invoker<Handle, Executable> invoker = invokers.get(0);
        Executable executable = invoker.asInterface();
        assertThat(executable.execute("dreamhead"), is("hello dreamhead"));
        assertThat(invoker.annotation().value(), is("runner"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_while_interface_has_many_methods() {
        delegate(Handle.class).to(ManyMethodInterface.class).with(new Runner());
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_while_annotated_method_has_different_return_type() {
        delegate(Handle.class).to(DifferentReturnTypeExecutable.class).with(new Runner());
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_while_annotated_method_has_different_size_of_parameter() {
        delegate(Handle.class).to(DifferentSizeParameterExecutable.class).with(new Runner());
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_while_annotated_parameter_has_different_type() {
        delegate(Handle.class).to(DifferentSizeParameterExecutable.class).with(new Runner());
    }

    @Test
    public void should_work_while_annotated_parameter_is_assigned_to_interface_parameter() {
        List<Invoker<Handle, ObjectExecutable>> invokers = delegate(Handle.class).to(ObjectExecutable.class).with(new Runner());
        ObjectExecutable executable = invokers.get(0).asInterface();
        assertThat(executable.execute("dreamhead"), is("hello dreamhead"));
    }

    @Test
    public void should_work_while_annotated_return_value_is_assigned_to_interface_return_value() {
        List<Invoker<Handle, ObjectReturnValueExecutable>> invokers = delegate(Handle.class).to(ObjectReturnValueExecutable.class).with(new Runner());
        ObjectReturnValueExecutable executable = invokers.get(0).asInterface();
        assertEquals("hello dreamhead", executable.execute("dreamhead"));
    }

    @Test
    public void should_trait_only_1_annotated_method() {
        Executable executable = delegate(Handle.class).to(Executable.class).trait(new Runner());
        assertThat(executable.execute("dreamhead"), is("hello dreamhead"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_while_trait_instance_with_many_annotated_method() {
        delegate(Handle.class).to(Executable.class).trait(new MultipleAnnotatedMethodRunner());
    }
}