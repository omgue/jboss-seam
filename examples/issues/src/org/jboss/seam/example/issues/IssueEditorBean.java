package org.jboss.seam.example.issues;
// Generated Dec 27, 2005 10:28:30 AM by Hibernate Tools 3.1.0 beta3

import static javax.ejb.TransactionAttributeType.NOT_SUPPORTED;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.persistence.EntityManager;

import org.hibernate.validator.Valid;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Outcome;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.core.FacesMessages;


@Name("issueEditor")
@Stateful
@CheckLoggedIn
public class IssueEditorBean implements IssueEditor {

    @In(create=true)
    private EntityManager entityManager;

    @Valid
    private Issue issue;
    
    @TransactionAttribute(NOT_SUPPORTED)
    public Issue getInstance() {
       return issue;
    }
    
    @In(required=false)
    private Login login;
        
    @Create
    public void initialize()
    {
       issue = new Issue();
       issue.setSubmitted( new Date() );
       if ( login!=null )
       {
          issue.setUser( login.getInstance() );
       }
    }

    private boolean isNew = true;
    
    @TransactionAttribute(NOT_SUPPORTED)
    public boolean isNew() {
       return isNew;
    }
    
    private String doneOutcome = "findIssue";

    @In(required=false)
    private transient IssueFinder issueFinder;

    @In(required=false)
    private transient ProjectFinder projectFinder;
    
    @LoggedIn
    @Begin(join=true)
    @IfInvalid(outcome=Outcome.REDISPLAY)
    public String create() {
       entityManager.persist(issue);
       isNew = false;
       if (issue.getProject()!=null) {
          issue.getProject().getIssues().add(issue);
       }
       refreshFinder();
       refreshProjectFinder();
       return "editIssue";
    }

    private void refreshProjectFinder() {
       if (projectFinder!=null) projectFinder.refresh();
    }
    
    @TransactionAttribute(NOT_SUPPORTED)
    public String getDescription() {
       String projectName = issue.getProject().getName();
       return issue.getId()==null ?
             "New Issue for Project [" + projectName + "]" :
             "Issue [" + issue.getId() + "] for Project [" + projectName + "]";
    }
    
    @LoggedIn
    @IfInvalid(outcome=Outcome.REDISPLAY, refreshEntities=true)
    public String update() 
    {
       refreshFinder();
       return null;
    }
    
    @End
    @LoggedIn
    public String delete() {
       entityManager.remove(issue);
       issue.getProject().getIssues().remove(issue);
       refreshFinder();
       refreshProjectFinder();
       return doneOutcome;
    }

    @End
    public String done() {
       if (!isNew) entityManager.refresh(issue);
       return doneOutcome;
    }
    
    private void refreshFinder() {
       if (issueFinder!=null) issueFinder.refresh();
    }

    @In(create=true)
    private transient ProjectEditor projectEditor;

    @DataModel
    public List getCommentsList() {
       return issue == null || issue.getComments()==null ?
             null : new ArrayList( issue.getComments() );
    }

    @DataModelSelection
    private Comment selectedComment;
    
    public Comment getSelectedComment()
    {
       return selectedComment;
    }
    
    @Begin
    public String select() {
       issue = issueFinder.getSelection();
       isNew = false;
       return "editIssue";
    }
    
    @LoggedIn
    @Begin(nested=true)
    public String createIssue() {
       isNew = true;
       issue = new Issue();
       issue.setUser( login.getInstance() );
       issue.setSubmitted( new Date() );
       issue.setProject( projectEditor.getInstance() );
       doneOutcome = "editProject";
       return "editIssue";
    }
    
    @Begin(nested=true)
    public String selectIssue() {
       isNew = false;
       issue = projectEditor.getSelectedIssue();
       doneOutcome = "editProject";
       return "editIssue";
    }
    
    @Out(scope=ScopeType.EVENT, required=false)
    private String developer;
    
    @TransactionAttribute(NOT_SUPPORTED)
    public void setDeveloper(String developer)
    {
       this.developer = developer;
    }
    
    @TransactionAttribute(NOT_SUPPORTED)
    public String getDeveloper()
    {
       return developer;
    }
    
    public String unassignDeveloper()
    {
       issue.setAssigned(null);
       return null;
    }
    
    public String assignDeveloper()
    {
       User user = entityManager.find(User.class, developer);
       if (user==null)
       {
          FacesMessages.instance().addFromResourceBundle("UserNotFound");
       }
       else if ( !issue.getProject().getDevelopers().contains(user) )
       {
          FacesMessages.instance().addFromResourceBundle("UserNotADeveloper");
       }
       else
       {
          issue.setAssigned(user);
          developer = null;
       }
       return null;
    }
    
    public String resolve()
    {
       issue.setStatus(IssueStatus.RESOLVED);
       return null;
    }
    
    @TransactionAttribute(NOT_SUPPORTED)
    public boolean isOpen()
    {
       return issue.getStatus()!=IssueStatus.RESOLVED;
    }

    @Destroy @Remove
    public void destroy() {}
    
}