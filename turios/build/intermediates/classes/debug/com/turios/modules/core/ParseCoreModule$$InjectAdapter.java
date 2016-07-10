// Code generated by dagger-compiler.  Do not edit.
package com.turios.modules.core;

import dagger.internal.Binding;
import dagger.internal.Linker;
import java.util.Set;
import javax.inject.Provider;

/**
 * A {@code Binding<ParseCoreModule>} implementation which satisfies
 * Dagger's infrastructure requirements including:
 *
 * Owning the dependency links between {@code ParseCoreModule} and its
 * dependencies.
 *
 * Being a {@code Provider<ParseCoreModule>} and handling creation and
 * preparation of object instances.
 */
public final class ParseCoreModule$$InjectAdapter extends Binding<ParseCoreModule>
    implements Provider<ParseCoreModule> {
  private Binding<android.content.Context> context;
  private Binding<com.turios.persistence.Preferences> preferences;

  public ParseCoreModule$$InjectAdapter() {
    super("com.turios.modules.core.ParseCoreModule", "members/com.turios.modules.core.ParseCoreModule", IS_SINGLETON, ParseCoreModule.class);
  }

  /**
   * Used internally to link bindings/providers together at run time
   * according to their dependency graph.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void attach(Linker linker) {
    context = (Binding<android.content.Context>) linker.requestBinding("@com.turios.dagger.quialifiers.ForApplication()/android.content.Context", ParseCoreModule.class, getClass().getClassLoader());
    preferences = (Binding<com.turios.persistence.Preferences>) linker.requestBinding("com.turios.persistence.Preferences", ParseCoreModule.class, getClass().getClassLoader());
  }

  /**
   * Used internally obtain dependency information, such as for cyclical
   * graph detection.
   */
  @Override
  public void getDependencies(Set<Binding<?>> getBindings, Set<Binding<?>> injectMembersBindings) {
    getBindings.add(context);
    getBindings.add(preferences);
  }

  /**
   * Returns the fully provisioned instance satisfying the contract for
   * {@code Provider<ParseCoreModule>}.
   */
  @Override
  public ParseCoreModule get() {
    ParseCoreModule result = new ParseCoreModule(context.get(), preferences.get());
    return result;
  }

}
