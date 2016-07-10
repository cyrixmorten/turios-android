// Code generated by dagger-compiler.  Do not edit.
package com.turios.activities;

import dagger.MembersInjector;
import dagger.internal.Binding;
import dagger.internal.Linker;
import java.util.Set;
import javax.inject.Provider;

/**
 * A {@code Binding<TuriosActionBarController>} implementation which satisfies
 * Dagger's infrastructure requirements including:
 *
 * Owning the dependency links between {@code TuriosActionBarController} and its
 * dependencies.
 *
 * Being a {@code Provider<TuriosActionBarController>} and handling creation and
 * preparation of object instances.
 *
 * Being a {@code MembersInjector<TuriosActionBarController>} and handling injection
 * of annotated fields.
 */
public final class TuriosActionBarController$$InjectAdapter extends Binding<TuriosActionBarController>
    implements Provider<TuriosActionBarController>, MembersInjector<TuriosActionBarController> {
  private Binding<com.turios.dagger.DaggerActivity> field_mActivity;
  private Binding<android.app.ActionBar> field_mActionbar;
  private Binding<android.view.MenuInflater> field_mMenuInflater;
  private Binding<com.turios.persistence.Preferences> field_preferences;
  private Binding<com.turios.activities.util.WakeScreen> field_wakeScreen;
  private Binding<com.turios.util.Device> field_device;
  private Binding<com.turios.modules.core.ParseCoreModule> field_parse;
  private Binding<com.turios.modules.core.ExpirationCoreModule> field_expiration;
  private Binding<com.turios.modules.core.PathsCoreModule> field_paths;
  private Binding<com.turios.modules.core.DisplayCoreModule> field_display;
  private Binding<com.turios.modules.core.LocationsCoreModule> field_locationService;
  private Binding<com.turios.modules.extend.BasisModule> field_basisModule;
  private Binding<com.turios.modules.extend.BrowserModule> field_browserModule;
  private Binding<com.turios.modules.extend.DropboxModule> field_dropboxModule;
  private Binding<com.turios.modules.extend.GoogleMapsModule> field_googleMapsModule;
  private Binding<com.turios.modules.extend.HydrantsModule> field_hydrantsModule;
  private Binding<android.support.v4.app.FragmentManager> field_fm;
  private Binding<android.os.Handler> field_handler;
  private Binding<android.content.Context> field_context;
  private Binding<android.app.ActionBar.TabListener> parameter_mTabListener;
  private Binding<com.turios.activities.listeners.TuriosUICallback> parameter_mTuriosUICallback;

  public TuriosActionBarController$$InjectAdapter() {
    super("com.turios.activities.TuriosActionBarController", "members/com.turios.activities.TuriosActionBarController", IS_SINGLETON, TuriosActionBarController.class);
  }

  /**
   * Used internally to link bindings/providers together at run time
   * according to their dependency graph.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void attach(Linker linker) {
    parameter_mTabListener = (Binding<android.app.ActionBar.TabListener>) linker.requestBinding("android.app.ActionBar$TabListener", TuriosActionBarController.class, getClass().getClassLoader());
    parameter_mTuriosUICallback = (Binding<com.turios.activities.listeners.TuriosUICallback>) linker.requestBinding("com.turios.activities.listeners.TuriosUICallback", TuriosActionBarController.class, getClass().getClassLoader());
    field_mActivity = (Binding<com.turios.dagger.DaggerActivity>) linker.requestBinding("com.turios.dagger.DaggerActivity", TuriosActionBarController.class, getClass().getClassLoader());
    field_mActionbar = (Binding<android.app.ActionBar>) linker.requestBinding("android.app.ActionBar", TuriosActionBarController.class, getClass().getClassLoader());
    field_mMenuInflater = (Binding<android.view.MenuInflater>) linker.requestBinding("android.view.MenuInflater", TuriosActionBarController.class, getClass().getClassLoader());
    field_preferences = (Binding<com.turios.persistence.Preferences>) linker.requestBinding("com.turios.persistence.Preferences", TuriosActionBarController.class, getClass().getClassLoader());
    field_wakeScreen = (Binding<com.turios.activities.util.WakeScreen>) linker.requestBinding("com.turios.activities.util.WakeScreen", TuriosActionBarController.class, getClass().getClassLoader());
    field_device = (Binding<com.turios.util.Device>) linker.requestBinding("com.turios.util.Device", TuriosActionBarController.class, getClass().getClassLoader());
    field_parse = (Binding<com.turios.modules.core.ParseCoreModule>) linker.requestBinding("com.turios.modules.core.ParseCoreModule", TuriosActionBarController.class, getClass().getClassLoader());
    field_expiration = (Binding<com.turios.modules.core.ExpirationCoreModule>) linker.requestBinding("com.turios.modules.core.ExpirationCoreModule", TuriosActionBarController.class, getClass().getClassLoader());
    field_paths = (Binding<com.turios.modules.core.PathsCoreModule>) linker.requestBinding("com.turios.modules.core.PathsCoreModule", TuriosActionBarController.class, getClass().getClassLoader());
    field_display = (Binding<com.turios.modules.core.DisplayCoreModule>) linker.requestBinding("com.turios.modules.core.DisplayCoreModule", TuriosActionBarController.class, getClass().getClassLoader());
    field_locationService = (Binding<com.turios.modules.core.LocationsCoreModule>) linker.requestBinding("com.turios.modules.core.LocationsCoreModule", TuriosActionBarController.class, getClass().getClassLoader());
    field_basisModule = (Binding<com.turios.modules.extend.BasisModule>) linker.requestBinding("com.turios.modules.extend.BasisModule", TuriosActionBarController.class, getClass().getClassLoader());
    field_browserModule = (Binding<com.turios.modules.extend.BrowserModule>) linker.requestBinding("com.turios.modules.extend.BrowserModule", TuriosActionBarController.class, getClass().getClassLoader());
    field_dropboxModule = (Binding<com.turios.modules.extend.DropboxModule>) linker.requestBinding("com.turios.modules.extend.DropboxModule", TuriosActionBarController.class, getClass().getClassLoader());
    field_googleMapsModule = (Binding<com.turios.modules.extend.GoogleMapsModule>) linker.requestBinding("com.turios.modules.extend.GoogleMapsModule", TuriosActionBarController.class, getClass().getClassLoader());
    field_hydrantsModule = (Binding<com.turios.modules.extend.HydrantsModule>) linker.requestBinding("com.turios.modules.extend.HydrantsModule", TuriosActionBarController.class, getClass().getClassLoader());
    field_fm = (Binding<android.support.v4.app.FragmentManager>) linker.requestBinding("android.support.v4.app.FragmentManager", TuriosActionBarController.class, getClass().getClassLoader());
    field_handler = (Binding<android.os.Handler>) linker.requestBinding("android.os.Handler", TuriosActionBarController.class, getClass().getClassLoader());
    field_context = (Binding<android.content.Context>) linker.requestBinding("@com.turios.dagger.quialifiers.ForActivity()/android.content.Context", TuriosActionBarController.class, getClass().getClassLoader());
  }

  /**
   * Used internally obtain dependency information, such as for cyclical
   * graph detection.
   */
  @Override
  public void getDependencies(Set<Binding<?>> getBindings, Set<Binding<?>> injectMembersBindings) {
    getBindings.add(parameter_mTabListener);
    getBindings.add(parameter_mTuriosUICallback);
    injectMembersBindings.add(field_mActivity);
    injectMembersBindings.add(field_mActionbar);
    injectMembersBindings.add(field_mMenuInflater);
    injectMembersBindings.add(field_preferences);
    injectMembersBindings.add(field_wakeScreen);
    injectMembersBindings.add(field_device);
    injectMembersBindings.add(field_parse);
    injectMembersBindings.add(field_expiration);
    injectMembersBindings.add(field_paths);
    injectMembersBindings.add(field_display);
    injectMembersBindings.add(field_locationService);
    injectMembersBindings.add(field_basisModule);
    injectMembersBindings.add(field_browserModule);
    injectMembersBindings.add(field_dropboxModule);
    injectMembersBindings.add(field_googleMapsModule);
    injectMembersBindings.add(field_hydrantsModule);
    injectMembersBindings.add(field_fm);
    injectMembersBindings.add(field_handler);
    injectMembersBindings.add(field_context);
  }

  /**
   * Returns the fully provisioned instance satisfying the contract for
   * {@code Provider<TuriosActionBarController>}.
   */
  @Override
  public TuriosActionBarController get() {
    TuriosActionBarController result = new TuriosActionBarController(parameter_mTabListener.get(), parameter_mTuriosUICallback.get());
    injectMembers(result);
    return result;
  }

  /**
   * Injects any {@code @Inject} annotated fields in the given instance,
   * satisfying the contract for {@code Provider<TuriosActionBarController>}.
   */
  @Override
  public void injectMembers(TuriosActionBarController object) {
    object.mActivity = field_mActivity.get();
    object.mActionbar = field_mActionbar.get();
    object.mMenuInflater = field_mMenuInflater.get();
    object.preferences = field_preferences.get();
    object.wakeScreen = field_wakeScreen.get();
    object.device = field_device.get();
    object.parse = field_parse.get();
    object.expiration = field_expiration.get();
    object.paths = field_paths.get();
    object.display = field_display.get();
    object.locationService = field_locationService.get();
    object.basisModule = field_basisModule.get();
    object.browserModule = field_browserModule.get();
    object.dropboxModule = field_dropboxModule.get();
    object.googleMapsModule = field_googleMapsModule.get();
    object.hydrantsModule = field_hydrantsModule.get();
    object.fm = field_fm.get();
    object.handler = field_handler.get();
    object.context = field_context.get();
  }

}
