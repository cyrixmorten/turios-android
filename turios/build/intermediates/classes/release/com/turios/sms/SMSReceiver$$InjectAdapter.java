// Code generated by dagger-compiler.  Do not edit.
package com.turios.sms;

import dagger.MembersInjector;
import dagger.internal.Binding;
import dagger.internal.Linker;
import java.util.Set;
import javax.inject.Provider;

/**
 * A {@code Binding<SMSReceiver>} implementation which satisfies
 * Dagger's infrastructure requirements including:
 *
 * Owning the dependency links between {@code SMSReceiver} and its
 * dependencies.
 *
 * Being a {@code Provider<SMSReceiver>} and handling creation and
 * preparation of object instances.
 *
 * Being a {@code MembersInjector<SMSReceiver>} and handling injection
 * of annotated fields.
 */
public final class SMSReceiver$$InjectAdapter extends Binding<SMSReceiver>
    implements Provider<SMSReceiver>, MembersInjector<SMSReceiver> {
  private Binding<com.turios.modules.core.ParseCoreModule> parse;
  private Binding<com.turios.modules.core.DisplayCoreModule> display;
  private Binding<com.turios.modules.extend.BasisModule> basis;
  private Binding<com.turios.dagger.DaggerBroadcastReceiver> supertype;

  public SMSReceiver$$InjectAdapter() {
    super("com.turios.sms.SMSReceiver", "members/com.turios.sms.SMSReceiver", NOT_SINGLETON, SMSReceiver.class);
  }

  /**
   * Used internally to link bindings/providers together at run time
   * according to their dependency graph.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void attach(Linker linker) {
    parse = (Binding<com.turios.modules.core.ParseCoreModule>) linker.requestBinding("com.turios.modules.core.ParseCoreModule", SMSReceiver.class, getClass().getClassLoader());
    display = (Binding<com.turios.modules.core.DisplayCoreModule>) linker.requestBinding("com.turios.modules.core.DisplayCoreModule", SMSReceiver.class, getClass().getClassLoader());
    basis = (Binding<com.turios.modules.extend.BasisModule>) linker.requestBinding("com.turios.modules.extend.BasisModule", SMSReceiver.class, getClass().getClassLoader());
    supertype = (Binding<com.turios.dagger.DaggerBroadcastReceiver>) linker.requestBinding("members/com.turios.dagger.DaggerBroadcastReceiver", SMSReceiver.class, getClass().getClassLoader(), false, true);
  }

  /**
   * Used internally obtain dependency information, such as for cyclical
   * graph detection.
   */
  @Override
  public void getDependencies(Set<Binding<?>> getBindings, Set<Binding<?>> injectMembersBindings) {
    injectMembersBindings.add(parse);
    injectMembersBindings.add(display);
    injectMembersBindings.add(basis);
    injectMembersBindings.add(supertype);
  }

  /**
   * Returns the fully provisioned instance satisfying the contract for
   * {@code Provider<SMSReceiver>}.
   */
  @Override
  public SMSReceiver get() {
    SMSReceiver result = new SMSReceiver();
    injectMembers(result);
    return result;
  }

  /**
   * Injects any {@code @Inject} annotated fields in the given instance,
   * satisfying the contract for {@code Provider<SMSReceiver>}.
   */
  @Override
  public void injectMembers(SMSReceiver object) {
    object.parse = parse.get();
    object.display = display.get();
    object.basis = basis.get();
    supertype.injectMembers(object);
  }

}
