// Code generated by dagger-compiler.  Do not edit.
package com.turios.modules.extend;

import dagger.MembersInjector;
import dagger.internal.Binding;
import dagger.internal.Linker;
import java.util.Set;
import javax.inject.Provider;

/**
 * A {@code Binding<PicklistModule>} implementation which satisfies
 * Dagger's infrastructure requirements including:
 *
 * Owning the dependency links between {@code PicklistModule} and its
 * dependencies.
 *
 * Being a {@code Provider<PicklistModule>} and handling creation and
 * preparation of object instances.
 *
 * Being a {@code MembersInjector<PicklistModule>} and handling injection
 * of annotated fields.
 */
public final class PicklistModule$$InjectAdapter extends Binding<PicklistModule>
    implements Provider<PicklistModule>, MembersInjector<PicklistModule> {
  private Binding<android.content.Context> context;
  private Binding<com.turios.persistence.Preferences> preferences;
  private Binding<com.turios.modules.core.ExpirationCoreModule> expiration;
  private Binding<com.turios.modules.core.ParseCoreModule> parse;
  private Binding<com.turios.modules.core.PathsCoreModule> paths;
  private Binding<StandardModule> supertype;

  public PicklistModule$$InjectAdapter() {
    super("com.turios.modules.extend.PicklistModule", "members/com.turios.modules.extend.PicklistModule", IS_SINGLETON, PicklistModule.class);
  }

  /**
   * Used internally to link bindings/providers together at run time
   * according to their dependency graph.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void attach(Linker linker) {
    context = (Binding<android.content.Context>) linker.requestBinding("@com.turios.dagger.quialifiers.ForApplication()/android.content.Context", PicklistModule.class, getClass().getClassLoader());
    preferences = (Binding<com.turios.persistence.Preferences>) linker.requestBinding("com.turios.persistence.Preferences", PicklistModule.class, getClass().getClassLoader());
    expiration = (Binding<com.turios.modules.core.ExpirationCoreModule>) linker.requestBinding("com.turios.modules.core.ExpirationCoreModule", PicklistModule.class, getClass().getClassLoader());
    parse = (Binding<com.turios.modules.core.ParseCoreModule>) linker.requestBinding("com.turios.modules.core.ParseCoreModule", PicklistModule.class, getClass().getClassLoader());
    paths = (Binding<com.turios.modules.core.PathsCoreModule>) linker.requestBinding("com.turios.modules.core.PathsCoreModule", PicklistModule.class, getClass().getClassLoader());
    supertype = (Binding<StandardModule>) linker.requestBinding("members/com.turios.modules.extend.StandardModule", PicklistModule.class, getClass().getClassLoader(), false, true);
  }

  /**
   * Used internally obtain dependency information, such as for cyclical
   * graph detection.
   */
  @Override
  public void getDependencies(Set<Binding<?>> getBindings, Set<Binding<?>> injectMembersBindings) {
    getBindings.add(context);
    getBindings.add(preferences);
    getBindings.add(expiration);
    getBindings.add(parse);
    getBindings.add(paths);
    injectMembersBindings.add(supertype);
  }

  /**
   * Returns the fully provisioned instance satisfying the contract for
   * {@code Provider<PicklistModule>}.
   */
  @Override
  public PicklistModule get() {
    PicklistModule result = new PicklistModule(context.get(), preferences.get(), expiration.get(), parse.get(), paths.get());
    injectMembers(result);
    return result;
  }

  /**
   * Injects any {@code @Inject} annotated fields in the given instance,
   * satisfying the contract for {@code Provider<PicklistModule>}.
   */
  @Override
  public void injectMembers(PicklistModule object) {
    supertype.injectMembers(object);
  }

}
