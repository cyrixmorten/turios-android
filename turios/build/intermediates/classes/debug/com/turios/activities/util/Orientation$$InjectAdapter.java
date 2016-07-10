// Code generated by dagger-compiler.  Do not edit.
package com.turios.activities.util;

import dagger.internal.Binding;
import dagger.internal.Linker;
import java.util.Set;
import javax.inject.Provider;

/**
 * A {@code Binding<Orientation>} implementation which satisfies
 * Dagger's infrastructure requirements including:
 *
 * Owning the dependency links between {@code Orientation} and its
 * dependencies.
 *
 * Being a {@code Provider<Orientation>} and handling creation and
 * preparation of object instances.
 */
public final class Orientation$$InjectAdapter extends Binding<Orientation>
    implements Provider<Orientation> {
  private Binding<android.app.Activity> activity;

  public Orientation$$InjectAdapter() {
    super("com.turios.activities.util.Orientation", "members/com.turios.activities.util.Orientation", NOT_SINGLETON, Orientation.class);
  }

  /**
   * Used internally to link bindings/providers together at run time
   * according to their dependency graph.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void attach(Linker linker) {
    activity = (Binding<android.app.Activity>) linker.requestBinding("android.app.Activity", Orientation.class, getClass().getClassLoader());
  }

  /**
   * Used internally obtain dependency information, such as for cyclical
   * graph detection.
   */
  @Override
  public void getDependencies(Set<Binding<?>> getBindings, Set<Binding<?>> injectMembersBindings) {
    getBindings.add(activity);
  }

  /**
   * Returns the fully provisioned instance satisfying the contract for
   * {@code Provider<Orientation>}.
   */
  @Override
  public Orientation get() {
    Orientation result = new Orientation(activity.get());
    return result;
  }

}