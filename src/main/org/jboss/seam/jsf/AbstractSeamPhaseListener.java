package org.jboss.seam.jsf;

import static javax.faces.event.PhaseId.ANY_PHASE;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.model.DataModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.ContextAdaptor;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.ConversationList;
import org.jboss.seam.core.ConversationStack;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.FacesPage;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Pageflow;
import org.jboss.seam.core.Pages;
import org.jboss.seam.core.Switcher;
import org.jboss.seam.util.Transactions;

public abstract class AbstractSeamPhaseListener implements PhaseListener
{
   
   private static final Log log = LogFactory.getLog(AbstractSeamPhaseListener.class);
   
   public PhaseId getPhaseId()
   {
      return ANY_PHASE;
   }

   /**
    * Restore the page and conversation contexts during a JSF request
    */
   public void afterRestoreView(FacesContext facesContext)
   {
      Lifecycle.resumePage();
      Map parameters = facesContext.getExternalContext().getRequestParameterMap();
      boolean conversationFound = Manager.instance().restoreConversation(parameters);
      Lifecycle.resumeConversation( facesContext.getExternalContext() );
      if (!conversationFound)
      {
         Manager.instance().redirectToNoConversationView();
      }
      if ( Init.instance().isJbpmInstalled() )
      {
         Pageflow.instance().validatePageflow();
      }
      Manager.instance().handleConversationPropagation(parameters);
      
      if ( log.isDebugEnabled() )
      {
         log.debug( "After restoring conversation context: " + Contexts.getConversationContext() );
      }
   }
   
   /**
    * Look for a DataModel row selection in the request parameters,
    * and apply it to the DataModel.
    * 
    * @param parameters the request parameters
    */
   private void selectDataModelRow(Map parameters)
   {
      String dataModelSelection = (String) parameters.get("dataModelSelection");
      if (dataModelSelection!=null)
      {
         int colonLoc = dataModelSelection.indexOf(':');
         int bracketLoc = dataModelSelection.indexOf('[');
         if (colonLoc>0 && bracketLoc>colonLoc)
         {
            String var = dataModelSelection.substring(0, colonLoc);
            String name = dataModelSelection.substring(colonLoc+1, bracketLoc);
            int index = Integer.parseInt( dataModelSelection.substring( bracketLoc+1, dataModelSelection.length()-1 ) );
            Object value = Contexts.lookupInStatefulContexts(name);
            if (value!=null)
            {
               DataModel dataModel = (DataModel) value;
               dataModel.setRowIndex(index);
               Contexts.getEventContext().set( var, dataModel.getRowData() );
            }
         }
      }
   }

   protected void beforeUpdateModelValues(PhaseEvent event)
   {
      Pages.instance().applyViewRootValues( event.getFacesContext() );
      Manager.instance().setUpdateModelValuesCalled(true);
   }

   /**
    * Give the subclasses an opportunity to do stuff
    */
   protected void afterInvokeApplication() {}

   /**
    * Add a faces message when Seam-managed transactions fail.
    */
   protected void addTransactionFailedMessage()
   {
      try
      {
         if ( Transactions.isTransactionMarkedRollback() )
         {
            FacesMessages.instance().addFromResourceBundle(
                     FacesMessage.SEVERITY_WARN, 
                     "org.jboss.seam.TransactionFailed", 
                     "Transaction failed"
                  );
         }
      }
      catch (Exception e) {} //swallow silently, not important
   }

   protected void beforeRender(PhaseEvent event)
   {  
      
      FacesContext facesContext = event.getFacesContext();

      if ( !Manager.instance().isUpdateModelValuesCalled() )
      {
         Pages.instance().applyRequestParameterValues(facesContext);
      }
      
      if ( Contexts.isPageContextActive() )
      {
         Context pageContext = Contexts.getPageContext();
         pageContext.flush();
         //force refresh of the conversation lists (they are kept in PAGE context)
         pageContext.remove( Seam.getComponentName(ConversationList.class) );
         pageContext.remove( Seam.getComponentName(Switcher.class) );
         pageContext.remove( Seam.getComponentName(ConversationStack.class) );
      }

      selectDataModelRow( facesContext.getExternalContext().getRequestParameterMap() );
      
      callPageActions(event);
      
      if ( facesContext.getResponseComplete() )
      {
         //workaround for a bug in MyFaces prior to 1.1.3
         if ( Init.instance().isMyFacesLifecycleBug() ) 
         {
            Lifecycle.endRequest( facesContext.getExternalContext() );
         }
      }
      else //if the page actions did not call responseComplete()
      {
         FacesMessages.instance().beforeRenderResponse();
         //do this both before and after render, since conversations 
         //and pageflows can begin during render
         Manager.instance().prepareBackswitch(facesContext); 
      }
      
      FacesPage.instance().storeConversation();
      FacesPage.instance().storePageflow();
      
   }
   
   protected void afterRender(FacesContext facesContext)
   {
      //do this both before and after render, since conversations 
      //and pageflows can begin during render
      Manager.instance().prepareBackswitch(facesContext);
      
      ExternalContext externalContext = facesContext.getExternalContext();
      Manager.instance().endRequest( ContextAdaptor.getSession(externalContext) );
      Lifecycle.endRequest(externalContext);
   }

   protected void afterResponseComplete(FacesContext facesContext)
   {
      //responseComplete() was called by one of the other phases, 
      //so we will never get to the RENDER_RESPONSE phase
      //Note: we can't call Manager.instance().beforeRedirect() here, 
      //since a redirect is not the only reason for a responseComplete
      ExternalContext externalContext = facesContext.getExternalContext();
      Manager.instance().endRequest( ContextAdaptor.getSession(externalContext) );
      Lifecycle.endRequest( facesContext.getExternalContext() );
   }

   private boolean callPageActions(PhaseEvent event)
   {
      Lifecycle.setPhaseId( PhaseId.INVOKE_APPLICATION );
      boolean actionsWereCalled = false;
      try
      {
         actionsWereCalled = Pages.callAction( event.getFacesContext() ) || actionsWereCalled;
         actionsWereCalled = Pages.instance().callAction() || actionsWereCalled;
         return actionsWereCalled;
      }
      finally
      {
         Lifecycle.setPhaseId( PhaseId.RENDER_RESPONSE );
         if (actionsWereCalled) 
         {
            FacesMessages.afterPhase();
            handleTransactionsAfterPageActions(event); //TODO: does it really belong in the finally?
         }
      }
   }
   
   protected void handleTransactionsAfterPageActions(PhaseEvent event) {}
   
   private static boolean exists = false;
   
   protected AbstractSeamPhaseListener()
   {
      if (exists) log.warn("There should only be one Seam phase listener per application");
      exists=true;
   }

   /////////Do not really belong here:
   
   void begin(PhaseId phaseId) {
      try 
      {
         if ( !Transactions.isTransactionActiveOrMarkedRollback() )
         {
            log.debug("beginning transaction prior to phase: " + phaseId);
            Transactions.getUserTransaction().begin();
         }
      }
      catch (Exception e)
      {
         //TODO: what should we *really* do here??
         throw new IllegalStateException("Could not start transaction", e);
      }
   }

   void commitOrRollback(PhaseId phaseId) {
      try 
      {
         if ( Transactions.isTransactionActive() )
         {
            log.debug("committing transaction after phase: " + phaseId);
            Transactions.getUserTransaction().commit();
         }
         else if ( Transactions.isTransactionMarkedRollback() )
         {
            log.debug("rolling back transaction after phase: " + phaseId);
            Transactions.getUserTransaction().rollback();
         }
      }
      catch (Exception e)
      {
         //TODO: what should we *really* do here??
         throw new IllegalStateException("Could not commit transaction", e);
      }
   }

}
