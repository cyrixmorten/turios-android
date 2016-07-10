// Code generated by dagger-compiler.  Do not edit.
package com.turios.settings.modules;

import dagger.MembersInjector;
import dagger.internal.Binding;
import dagger.internal.Linker;
import java.util.Set;
import javax.inject.Provider;

/**
 * A {@code Binding<PicklistSettings>} implementation which satisfies
 * Dagger's infrastructure requirements including:
 *
 * Owning the dependency links between {@code PicklistSettings} and its
 * dependencies.
 *
 * Being a {@code Provider<PicklistSettings>} and handling creation and
 * preparation of object instances.
 *
 * Being a {@code MembersInjector<PicklistSettings>} and handling injection
 * of annotated fields.
 */
public final class PicklistSettings$$InjectAdapter extends Binding<PicklistSettings>
    implements Provider<PicklistSettings>, MembersInjector<PicklistSettings> {
  private Binding<com.turios.persistence.Preferences> preferences;
  private Binding<android.content.Context> context;
  private Binding<com.turios.modules.extend.PicklistModule> picklistModule;
  private Binding<com.turios.dagger.DaggerPreferenceFragment> supertype;

  public PicklistSettings$$InjectAdapter() {
    super("com.turios.settings.modules.PicklistSettings", "members/com.turios.settings.modules.PicklistSettings", NOT_SINGLETON, PicklistSettings.class);
  }

  /**
   * Used internally to link bindings/providers together at run time
   * according to their dependency graph.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void attach(Linker linker) {
    preferences = (Binding<com.turios.persistence.Preferences>) linker.requestBinding("com.turios.persistence.Preferences", PicklistSettings.class, getClass().getClassLoader());
    context = (Binding<android.content.Context>) linker.requestBinding("@com.turios.dagger.quialifiers.ForActivity()/android.content.Context", PicklistSettings.class, getClass().getClassLoader());
    picklistModule = (Binding<com.turios.modules.extend.PicklistModule>) linker.requestBinding("com.turios.modules.extend.PicklistModule", PicklistSettings.class, getClass().getClassLoader());
    supertype = (Binding<com.turios.dagger.DaggerPreferenceFragment>) linker.requestBinding("members/com.turios.dagger.DaggerPreferenceFragment", PicklistSettings.class, getClass().getClassLoader(), false, true);
  }

  /**
   * Used internally obtain dependency information, such as for cyclical
   * graph detection.
   */
  @Override
  public void getDependencies(Set<Binding<?>> getBindings, Set<Binding<?>> injectMembersBindings) {
    injectMembersBindings.add(preferences);
    injectMembersBindings.add(context);
    injectMembersBindings.add(picklistModule);
    injectMembersBindings.add(supertype);
  }

  /**
   * Returns the fully provisioned instance satisfying the contract for
   * {@code Provider<PicklistSettings>}.
   */
  @Override
  public PicklistSettings get() {
    PicklistSettings result = new PicklistSettings();
    injectMembers(result);
    return result;
  }

  /**
   * Injects any {@code @Inject} annotated fields in the given instance,
   * satisfying the contract for {@code Provider<PicklistSettings>}.
   */
  @Override
  public void injectMembers(PicklistSettings object) {
    object.preferences = preferences.get();
    object.context = context.get();
    object.picklistModule = picklistModule.get();
    supertype.injectMembers(object);
  }

}
