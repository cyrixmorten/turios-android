// Code generated by dagger-compiler.  Do not edit.
package com.turios.activities;

import dagger.MembersInjector;
import dagger.internal.Binding;
import dagger.internal.Linker;
import java.util.Set;
import javax.inject.Provider;

/**
 * A {@code Binding<Turios>} implementation which satisfies
 * Dagger's infrastructure requirements including:
 *
 * Owning the dependency links between {@code Turios} and its
 * dependencies.
 *
 * Being a {@code Provider<Turios>} and handling creation and
 * preparation of object instances.
 *
 * Being a {@code MembersInjector<Turios>} and handling injection
 * of annotated fields.
 */
public final class Turios$$InjectAdapter extends Binding<Turios>
    implements Provider<Turios>, MembersInjector<Turios> {
  private Binding<TuriosActionBarController> mTuriosActionBar;
  private Binding<android.support.v4.app.FragmentManager> fm;
  private Binding<com.turios.persistence.Preferences> preferences;
  private Binding<com.turios.activities.util.WakeScreen> wakeScreen;
  private Binding<com.turios.util.Device> device;
  private Binding<android.os.Handler> handler;
  private Binding<com.turios.modules.core.ParseCoreModule> parse;
  private Binding<com.turios.modules.core.ExpirationCoreModule> expiration;
  private Binding<com.turios.modules.core.PathsCoreModule> paths;
  private Binding<com.turios.modules.core.DisplayCoreModule> display;
  private Binding<com.turios.modules.core.LocationsCoreModule> locationService;
  private Binding<com.turios.modules.extend.AccessplansModule> accessplansModule;
  private Binding<com.turios.modules.extend.BasisModule> basisModule;
  private Binding<com.turios.modules.extend.BrowserModule> browserModule;
  private Binding<com.turios.modules.extend.DirectionsModule> directionsModule;
  private Binding<com.turios.modules.extend.DropboxModule> dropboxModule;
  private Binding<com.turios.modules.extend.GoogleMapsModule> googleMapsModule;
  private Binding<com.turios.modules.extend.HydrantsModule> hydrantsModule;
  private Binding<com.turios.modules.extend.PicklistModule> picklistModule;
  private Binding<com.turios.activities.listeners.TuriosExpirationListener> expirationListener;
  private Binding<com.turios.activities.listeners.TuriosDisplayListener> displayListener;
  private Binding<com.turios.activities.listeners.TuriosLocationsListener> locationsListener;
  private Binding<com.turios.activities.listeners.TuriosDropboxSyncListener> dropboxListener;
  private Binding<com.turios.activities.listeners.TuriosLocationListener> turiosLocationListenerImpl;
  private Binding<com.turios.dagger.DaggerActivity> supertype;

  public Turios$$InjectAdapter() {
    super("com.turios.activities.Turios", "members/com.turios.activities.Turios", NOT_SINGLETON, Turios.class);
  }

  /**
   * Used internally to link bindings/providers together at run time
   * according to their dependency graph.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void attach(Linker linker) {
    mTuriosActionBar = (Binding<TuriosActionBarController>) linker.requestBinding("com.turios.activities.TuriosActionBarController", Turios.class, getClass().getClassLoader());
    fm = (Binding<android.support.v4.app.FragmentManager>) linker.requestBinding("android.support.v4.app.FragmentManager", Turios.class, getClass().getClassLoader());
    preferences = (Binding<com.turios.persistence.Preferences>) linker.requestBinding("com.turios.persistence.Preferences", Turios.class, getClass().getClassLoader());
    wakeScreen = (Binding<com.turios.activities.util.WakeScreen>) linker.requestBinding("com.turios.activities.util.WakeScreen", Turios.class, getClass().getClassLoader());
    device = (Binding<com.turios.util.Device>) linker.requestBinding("com.turios.util.Device", Turios.class, getClass().getClassLoader());
    handler = (Binding<android.os.Handler>) linker.requestBinding("android.os.Handler", Turios.class, getClass().getClassLoader());
    parse = (Binding<com.turios.modules.core.ParseCoreModule>) linker.requestBinding("com.turios.modules.core.ParseCoreModule", Turios.class, getClass().getClassLoader());
    expiration = (Binding<com.turios.modules.core.ExpirationCoreModule>) linker.requestBinding("com.turios.modules.core.ExpirationCoreModule", Turios.class, getClass().getClassLoader());
    paths = (Binding<com.turios.modules.core.PathsCoreModule>) linker.requestBinding("com.turios.modules.core.PathsCoreModule", Turios.class, getClass().getClassLoader());
    display = (Binding<com.turios.modules.core.DisplayCoreModule>) linker.requestBinding("com.turios.modules.core.DisplayCoreModule", Turios.class, getClass().getClassLoader());
    locationService = (Binding<com.turios.modules.core.LocationsCoreModule>) linker.requestBinding("com.turios.modules.core.LocationsCoreModule", Turios.class, getClass().getClassLoader());
    accessplansModule = (Binding<com.turios.modules.extend.AccessplansModule>) linker.requestBinding("com.turios.modules.extend.AccessplansModule", Turios.class, getClass().getClassLoader());
    basisModule = (Binding<com.turios.modules.extend.BasisModule>) linker.requestBinding("com.turios.modules.extend.BasisModule", Turios.class, getClass().getClassLoader());
    browserModule = (Binding<com.turios.modules.extend.BrowserModule>) linker.requestBinding("com.turios.modules.extend.BrowserModule", Turios.class, getClass().getClassLoader());
    directionsModule = (Binding<com.turios.modules.extend.DirectionsModule>) linker.requestBinding("com.turios.modules.extend.DirectionsModule", Turios.class, getClass().getClassLoader());
    dropboxModule = (Binding<com.turios.modules.extend.DropboxModule>) linker.requestBinding("com.turios.modules.extend.DropboxModule", Turios.class, getClass().getClassLoader());
    googleMapsModule = (Binding<com.turios.modules.extend.GoogleMapsModule>) linker.requestBinding("com.turios.modules.extend.GoogleMapsModule", Turios.class, getClass().getClassLoader());
    hydrantsModule = (Binding<com.turios.modules.extend.HydrantsModule>) linker.requestBinding("com.turios.modules.extend.HydrantsModule", Turios.class, getClass().getClassLoader());
    picklistModule = (Binding<com.turios.modules.extend.PicklistModule>) linker.requestBinding("com.turios.modules.extend.PicklistModule", Turios.class, getClass().getClassLoader());
    expirationListener = (Binding<com.turios.activities.listeners.TuriosExpirationListener>) linker.requestBinding("com.turios.activities.listeners.TuriosExpirationListener", Turios.class, getClass().getClassLoader());
    displayListener = (Binding<com.turios.activities.listeners.TuriosDisplayListener>) linker.requestBinding("com.turios.activities.listeners.TuriosDisplayListener", Turios.class, getClass().getClassLoader());
    locationsListener = (Binding<com.turios.activities.listeners.TuriosLocationsListener>) linker.requestBinding("com.turios.activities.listeners.TuriosLocationsListener", Turios.class, getClass().getClassLoader());
    dropboxListener = (Binding<com.turios.activities.listeners.TuriosDropboxSyncListener>) linker.requestBinding("com.turios.activities.listeners.TuriosDropboxSyncListener", Turios.class, getClass().getClassLoader());
    turiosLocationListenerImpl = (Binding<com.turios.activities.listeners.TuriosLocationListener>) linker.requestBinding("com.turios.activities.listeners.TuriosLocationListener", Turios.class, getClass().getClassLoader());
    supertype = (Binding<com.turios.dagger.DaggerActivity>) linker.requestBinding("members/com.turios.dagger.DaggerActivity", Turios.class, getClass().getClassLoader(), false, true);
  }

  /**
   * Used internally obtain dependency information, such as for cyclical
   * graph detection.
   */
  @Override
  public void getDependencies(Set<Binding<?>> getBindings, Set<Binding<?>> injectMembersBindings) {
    injectMembersBindings.add(mTuriosActionBar);
    injectMembersBindings.add(fm);
    injectMembersBindings.add(preferences);
    injectMembersBindings.add(wakeScreen);
    injectMembersBindings.add(device);
    injectMembersBindings.add(handler);
    injectMembersBindings.add(parse);
    injectMembersBindings.add(expiration);
    injectMembersBindings.add(paths);
    injectMembersBindings.add(display);
    injectMembersBindings.add(locationService);
    injectMembersBindings.add(accessplansModule);
    injectMembersBindings.add(basisModule);
    injectMembersBindings.add(browserModule);
    injectMembersBindings.add(directionsModule);
    injectMembersBindings.add(dropboxModule);
    injectMembersBindings.add(googleMapsModule);
    injectMembersBindings.add(hydrantsModule);
    injectMembersBindings.add(picklistModule);
    injectMembersBindings.add(expirationListener);
    injectMembersBindings.add(displayListener);
    injectMembersBindings.add(locationsListener);
    injectMembersBindings.add(dropboxListener);
    injectMembersBindings.add(turiosLocationListenerImpl);
    injectMembersBindings.add(supertype);
  }

  /**
   * Returns the fully provisioned instance satisfying the contract for
   * {@code Provider<Turios>}.
   */
  @Override
  public Turios get() {
    Turios result = new Turios();
    injectMembers(result);
    return result;
  }

  /**
   * Injects any {@code @Inject} annotated fields in the given instance,
   * satisfying the contract for {@code Provider<Turios>}.
   */
  @Override
  public void injectMembers(Turios object) {
    object.mTuriosActionBar = mTuriosActionBar.get();
    object.fm = fm.get();
    object.preferences = preferences.get();
    object.wakeScreen = wakeScreen.get();
    object.device = device.get();
    object.handler = handler.get();
    object.parse = parse.get();
    object.expiration = expiration.get();
    object.paths = paths.get();
    object.display = display.get();
    object.locationService = locationService.get();
    object.accessplansModule = accessplansModule.get();
    object.basisModule = basisModule.get();
    object.browserModule = browserModule.get();
    object.directionsModule = directionsModule.get();
    object.dropboxModule = dropboxModule.get();
    object.googleMapsModule = googleMapsModule.get();
    object.hydrantsModule = hydrantsModule.get();
    object.picklistModule = picklistModule.get();
    object.expirationListener = expirationListener.get();
    object.displayListener = displayListener.get();
    object.locationsListener = locationsListener.get();
    object.dropboxListener = dropboxListener.get();
    object.turiosLocationListenerImpl = turiosLocationListenerImpl.get();
    supertype.injectMembers(object);
  }

}