// Code generated by dagger-compiler.  Do not edit.
package com.turios.activities.util;

import dagger.internal.Binding;
import dagger.internal.Linker;
import java.util.Set;
import javax.inject.Provider;

/**
 * A {@code Binding<WakeScreen>} implementation which satisfies
 * Dagger's infrastructure requirements including:
 *
 * Owning the dependency links between {@code WakeScreen} and its
 * dependencies.
 *
 * Being a {@code Provider<WakeScreen>} and handling creation and
 * preparation of object instances.
 */
public final class WakeScreen$$InjectAdapter extends Binding<WakeScreen>
    implements Provider<WakeScreen> {
  private Binding<android.content.Context> context;
  private Binding<android.app.Activity> activity;
  private Binding<com.turios.modules.extend.BasisModule> basisModule;
  private Binding<com.turios.util.Notifications> notifications;

  public WakeScreen$$InjectAdapter() {
    super("com.turios.activities.util.WakeScreen", "members/com.turios.activities.util.WakeScreen", IS_SINGLETON, WakeScreen.class);
  }

  /**
   * Used internally to link bindings/providers together at run time
   * according to their dependency graph.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void attach(Linker linker) {
    context = (Binding<android.content.Context>) linker.requestBinding("@com.turios.dagger.quialifiers.ForApplication()/android.content.Context", WakeScreen.class, getClass().getClassLoader());
    activity = (Binding<android.app.Activity>) linker.requestBinding("android.app.Activity", WakeScreen.class, getClass().getClassLoader());
    basisModule = (Binding<com.turios.modules.extend.BasisModule>) linker.requestBinding("com.turios.modules.extend.BasisModule", WakeScreen.class, getClass().getClassLoader());
    notifications = (Binding<com.turios.util.Notifications>) linker.requestBinding("com.turios.util.Notifications", WakeScreen.class, getClass().getClassLoader());
  }

  /**
   * Used internally obtain dependency information, such as for cyclical
   * graph detection.
   */
  @Override
  public void getDependencies(Set<Binding<?>> getBindings, Set<Binding<?>> injectMembersBindings) {
    getBindings.add(context);
    getBindings.add(activity);
    getBindings.add(basisModule);
    getBindings.add(notifications);
  }

  /**
   * Returns the fully provisioned instance satisfying the contract for
   * {@code Provider<WakeScreen>}.
   */
  @Override
  public WakeScreen get() {
    WakeScreen result = new WakeScreen(context.get(), activity.get(), basisModule.get(), notifications.get());
    return result;
  }

}
