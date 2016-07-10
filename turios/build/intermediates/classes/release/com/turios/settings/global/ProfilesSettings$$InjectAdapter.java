// Code generated by dagger-compiler.  Do not edit.
package com.turios.settings.global;

import dagger.MembersInjector;
import dagger.internal.Binding;
import dagger.internal.Linker;
import java.util.Set;
import javax.inject.Provider;

/**
 * A {@code Binding<ProfilesSettings>} implementation which satisfies
 * Dagger's infrastructure requirements including:
 *
 * Owning the dependency links between {@code ProfilesSettings} and its
 * dependencies.
 *
 * Being a {@code Provider<ProfilesSettings>} and handling creation and
 * preparation of object instances.
 *
 * Being a {@code MembersInjector<ProfilesSettings>} and handling injection
 * of annotated fields.
 */
public final class ProfilesSettings$$InjectAdapter extends Binding<ProfilesSettings>
    implements Provider<ProfilesSettings>, MembersInjector<ProfilesSettings> {
  private Binding<com.turios.modules.core.ParseCoreModule> parse;
  private Binding<com.turios.dagger.DaggerPreferenceFragment> supertype;

  public ProfilesSettings$$InjectAdapter() {
    super("com.turios.settings.global.ProfilesSettings", "members/com.turios.settings.global.ProfilesSettings", NOT_SINGLETON, ProfilesSettings.class);
  }

  /**
   * Used internally to link bindings/providers together at run time
   * according to their dependency graph.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void attach(Linker linker) {
    parse = (Binding<com.turios.modules.core.ParseCoreModule>) linker.requestBinding("com.turios.modules.core.ParseCoreModule", ProfilesSettings.class, getClass().getClassLoader());
    supertype = (Binding<com.turios.dagger.DaggerPreferenceFragment>) linker.requestBinding("members/com.turios.dagger.DaggerPreferenceFragment", ProfilesSettings.class, getClass().getClassLoader(), false, true);
  }

  /**
   * Used internally obtain dependency information, such as for cyclical
   * graph detection.
   */
  @Override
  public void getDependencies(Set<Binding<?>> getBindings, Set<Binding<?>> injectMembersBindings) {
    injectMembersBindings.add(parse);
    injectMembersBindings.add(supertype);
  }

  /**
   * Returns the fully provisioned instance satisfying the contract for
   * {@code Provider<ProfilesSettings>}.
   */
  @Override
  public ProfilesSettings get() {
    ProfilesSettings result = new ProfilesSettings();
    injectMembers(result);
    return result;
  }

  /**
   * Injects any {@code @Inject} annotated fields in the given instance,
   * satisfying the contract for {@code Provider<ProfilesSettings>}.
   */
  @Override
  public void injectMembers(ProfilesSettings object) {
    object.parse = parse.get();
    supertype.injectMembers(object);
  }

}
