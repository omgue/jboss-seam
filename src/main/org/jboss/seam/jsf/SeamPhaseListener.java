/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.jsf;

import static javax.faces.event.PhaseId.ANY_PHASE;
import static javax.faces.event.PhaseId.RENDER_RESPONSE;
import static javax.faces.event.PhaseId.RESTORE_VIEW;

import java.io.IOException;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.application.StateManager;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.jboss.logging.Logger;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.contexts.Session;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Manager;

/**
 * Manages the thread/context associations throught the
 * lifecycle of a JSF request.
 *
 * @author Gavin King
 */
public class SeamPhaseListener implements PhaseListener
{

   private static Logger log = Logger.getLogger( SeamPhaseListener.class );
   
   private boolean setStateManager = false;
   
   private void setStateManager(FacesContext facesContext) 
   {
      if (setStateManager) return;
      Application app = facesContext.getApplication();
      StateManager stateManager = new StateManagerInterceptor( app.getStateManager() );
      app.setStateManager( stateManager);
      setStateManager = true;
   }

   public PhaseId getPhaseId()
   {
      return ANY_PHASE;
   }

   public void beforePhase(PhaseEvent event)
   {
      setStateManager( event.getFacesContext() );
      log.trace( "before phase: " + event.getPhaseId() );
      
      if ( event.getPhaseId() == RESTORE_VIEW )
      {
         Lifecycle.beginRequest( event.getFacesContext().getExternalContext() );
      }
      /*else if ( event.getPhaseId() == RENDER_RESPONSE )
      {
         beforeSaveState();
         Manager.instance().conversationTimeout( event.getFacesContext().getExternalContext() );
      }*/

      Lifecycle.setPhaseId( event.getPhaseId() );

   }

   public void afterPhase(PhaseEvent event)
   {
      log.trace( "after phase: " + event.getPhaseId() );

      Lifecycle.setPhaseId(null);

      if ( event.getPhaseId() == RESTORE_VIEW )
      {
         restoreAnyConversationContext( event );
      }
      else if ( event.getPhaseId() == RENDER_RESPONSE )
      {
         Lifecycle.endRequest( event.getFacesContext().getExternalContext() );
      }
   }
   
   /**
    * Called just before the StateManager serializes the component tree
    */
   private void beforeSaveState(FacesContext ctx) {
      if ( !Init.instance().isClientSideConversations() ) 
      {
         // difficult question: does this really need to happen before 
         // storeAnyConversationContext, or could it be done later
         Manager.instance().conversationTimeout( ctx.getExternalContext() );
      }
      storeAnyConversationContext(ctx);
   }

   private static void restoreAnyConversationContext(PhaseEvent event)
   {
      Manager.instance().restoreConversation( getAttributes( event.getFacesContext() ), getParameters(event) );
      Lifecycle.resumeConversation( event.getFacesContext().getExternalContext() );
      log.debug( "After restore view, conversation context: " + Contexts.getConversationContext() );
   }

   private static void storeAnyConversationContext(FacesContext ctx)
   {
      log.debug( "Before render response" );
      Lifecycle.flushClientConversation();
      if ( !Contexts.isConversationContextActive() )
      {
         log.debug( "No active conversation context" );
      }
      else
      {
         Session session = Session.getSession(ctx.getExternalContext(), true);
         Manager.instance().storeConversation( getAttributes(ctx), session );
      }
   }

   private static Map getParameters(PhaseEvent event) {
      return event.getFacesContext().getExternalContext().getRequestParameterMap();
   }

   private static Map getAttributes(FacesContext facesContext)
   {
      return facesContext.getViewRoot().getAttributes();
   }
   
   /**
    * A wrapper for the JSF implementation's StateManager that allows
    * us to intercept saving of the serialized component tree. This
    * is quite ugly but was needed in order to allow conversations to
    * be started and manipulated during the RENDER_RESPONSE phase.
    * 
    * @author Gavin King
    */
   private final class StateManagerInterceptor extends StateManager {
      private final StateManager stateManager;

      private StateManagerInterceptor(StateManager sm) {
         this.stateManager = sm;
      }

      protected Object getComponentStateToSave(FacesContext ctx) {
         throw new UnsupportedOperationException();
      }

      protected Object getTreeStructureToSave(FacesContext ctx) {
         throw new UnsupportedOperationException();
      }

      protected void restoreComponentState(FacesContext ctx, UIViewRoot viewRoot, String str) {
         throw new UnsupportedOperationException();
      }

      protected UIViewRoot restoreTreeStructure(FacesContext ctx, String str1, String str2) {
         throw new UnsupportedOperationException();
      }

      public UIViewRoot restoreView(FacesContext ctx, String str1, String str2) {
         return stateManager.restoreView(ctx, str1, str2);
      }

      public SerializedView saveSerializedView(FacesContext ctx) {
         beforeSaveState(ctx);
         return stateManager.saveSerializedView(ctx);
      }

      public void writeState(FacesContext ctx, SerializedView sv) throws IOException {
         stateManager.writeState(ctx, sv);
      }

      public boolean isSavingStateInClient(FacesContext ctx) {
         return stateManager.isSavingStateInClient(ctx);
      }
   }

}
