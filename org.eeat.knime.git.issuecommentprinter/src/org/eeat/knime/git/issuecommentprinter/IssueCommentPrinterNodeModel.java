package org.eeat.knime.git.issuecommentprinter;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import static org.eclipse.egit.github.core.service.IssueService.FILTER_STATE;
import static org.eclipse.egit.github.core.service.IssueService.STATE_CLOSED;
import static org.eclipse.egit.github.core.service.IssueService.STATE_OPEN;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.PullRequestService;

/**
 * This is the model implementation of MyIssueCommentPrinter.
 * 
 *
 * @author 
 */
public class IssueCommentPrinterNodeModel extends NodeModel {
    
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(IssueCommentPrinterNodeModel.class);
        
    /** the settings key which is used to retrieve and 
        store the settings (from the dialog or from a settings file)    
       (package visibility to be usable from the dialog). */
	static final String CFGKEY_COUNT = "Count";

    /** initial default count value. */
    static final int DEFAULT_COUNT = 100;

    // example value: the models count variable filled from the dialog 
    // and used in the models execution method. The default components of the
    // dialog work with "SettingsModels".
    private final SettingsModelIntegerBounded m_count =
        new SettingsModelIntegerBounded(IssueCommentPrinterNodeModel.CFGKEY_COUNT,
                    IssueCommentPrinterNodeModel.DEFAULT_COUNT,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);
    

    /**
     * Constructor for the node model.
     */
    protected IssueCommentPrinterNodeModel() {
    
        // TODO one incoming port and one outgoing port is assumed
        super(1, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

        // TODO do something here
        logger.info("Node Model Stub... this is not yet implemented !");
        
      ///connecting to the repo
        RepositoryId repo = RepositoryId.create("twitter", "bootstrap");
        GitHubClient client = new GitHubClient();
        // Update credentials
        client.setCredentials("happydust", "chlhczw3");
        //end of connecting
        
        
        // the data table spec of the single output table, 
        // the table will have three columns:
        DataColumnSpec[] allColSpecs = new DataColumnSpec[7];
        allColSpecs[0] = 
            new DataColumnSpecCreator("User ID", StringCell.TYPE).createSpec();
        allColSpecs[1] = 
            new DataColumnSpecCreator("Issue Comment Created at", StringCell.TYPE).createSpec();
        allColSpecs[2] = 
            new DataColumnSpecCreator("Issue ID", IntCell.TYPE).createSpec();
        allColSpecs[3] = 
            new DataColumnSpecCreator("Issue CommentID", IntCell.TYPE).createSpec();
        allColSpecs[4] = 
            new DataColumnSpecCreator("Issue Comment Body", StringCell.TYPE).createSpec();
        allColSpecs[5] = 
            new DataColumnSpecCreator("State", StringCell.TYPE).createSpec();
        allColSpecs[6] = 
            new DataColumnSpecCreator("Issue Number", IntCell.TYPE).createSpec();
        DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
        // the execution context will provide us with storage capacity, in this
        // case a data container to which we will add rows sequentially
        // Note, this container can also handle arbitrary big data tables, it
        // will buffer to disc if necessary.
        
        BufferedDataContainer container = exec.createDataContainer(outputSpec);
        ////TO print out the Issue Comment
        IssueService issueService = new IssueService(client);
        PullRequestService pullService = new PullRequestService(client);
        String state=STATE_OPEN;
        Map<String, String> filter = Collections.singletonMap(FILTER_STATE, state);
        int i=0;
        
        for (Collection<Issue> issues : issueService.pageIssues(repo, filter))
          for (Issue issue : issues) {
            if (issue.getPullRequest() != null
                && issue.getPullRequest().getHtmlUrl() != null)
             System.out.println("..");
            else
            {
            	System.out.println("this issue has no pull request");
            if (issue.getComments() > 0)
              for (Comment comment : issueService.getComments(repo,
                  issue.getNumber()))
              {
            	  i++;
                System.out.println(MessageFormat.format(
                    "{0} commented on issue {1}:\n  {2}", comment.getUser()
                        .getLogin(), issue.getNumber(), comment.getBody()));
                RowKey key = new RowKey("Row " + i);
                // the cells of the current row, the types of the cells must match
                // the column spec (see above)
                DataCell[] cells = new DataCell[7];
                cells[0] = new StringCell(comment.getUser().getLogin()); 
                
                String date=comment.getCreatedAt().toGMTString();
                int index=date.indexOf("G");
                String dateGMT=date.substring(0, index);
                
                cells[1] = new StringCell(dateGMT); 
                cells[2] = new IntCell((int)(issue.getId()));
                cells[3] = new IntCell((int)(comment.getId()));
                cells[4] = new StringCell(comment.getBody());
                cells[5] = new StringCell(issue.getState());
                cells[6] =new IntCell(issue.getNumber());
                DataRow row = new DefaultRow(key, cells);
                container.addRowToTable(row);
                
                // check if the execution monitor was canceled
                exec.checkCanceled();
                exec.setProgress(i / (double)m_count.getIntValue(), 
                    "Adding row " + i);
                }
            
            }
          }
        state=STATE_CLOSED;
        filter = Collections.singletonMap(FILTER_STATE, state);
        for (Collection<Issue> issues : issueService.pageIssues(repo, filter))
            for (Issue issue : issues) {
              if (issue.getPullRequest() != null
                  && issue.getPullRequest().getHtmlUrl() != null)
               System.out.println("..");
              else
              {
              	System.out.println("this issue has no pull request");
              if (issue.getComments() > 0)
                for (Comment comment : issueService.getComments(repo,
                    issue.getNumber()))
                {
              	  i++;
                  System.out.println(MessageFormat.format(
                      "{0} commented on issue {1}:\n  {2}", comment.getUser()
                          .getLogin(), issue.getNumber(), comment.getBody()));
                  RowKey key = new RowKey("Row " + i);
                  // the cells of the current row, the types of the cells must match
                  // the column spec (see above)
                  DataCell[] cells = new DataCell[7];
                  cells[0] = new StringCell(comment.getUser().getLogin()); 
                  String date=comment.getCreatedAt().toGMTString();
                  int index=date.indexOf("G");
                  String dateGMT=date.substring(0, index);
                  
                  cells[1] = new StringCell(dateGMT); 
                  cells[2] = new IntCell((int)(issue.getId()));
                  cells[3] = new IntCell((int)(comment.getId()));
                  cells[4] = new StringCell(comment.getBody());
                  cells[5] = new StringCell(issue.getState());
                  cells[6] =new IntCell(issue.getNumber());
                  DataRow row = new DefaultRow(key, cells);
                  container.addRowToTable(row);
                  
                  // check if the execution monitor was canceled
                  exec.checkCanceled();
                  exec.setProgress(i / (double)m_count.getIntValue(), 
                      "Adding row " + i);
                  }
              
              }
            }
        ///
       
        // let's add m_count rows to it
       /* for (int i = 0; i < m_count.getIntValue(); i++) {
            RowKey key = new RowKey("Row " + i);
            // the cells of the current row, the types of the cells must match
            // the column spec (see above)
            DataCell[] cells = new DataCell[3];
            cells[0] = new StringCell("String_" + i); 
            cells[1] = new DoubleCell(0.5 * i); 
            cells[2] = new IntCell(i);
            DataRow row = new DefaultRow(key, cells);
            container.addRowToTable(row);
            
            // check if the execution monitor was canceled
            exec.checkCanceled();
            exec.setProgress(i / (double)m_count.getIntValue(), 
                "Adding row " + i);
        }*/
        // once we are done, we close the container and return its table
        container.close();
        BufferedDataTable out = container.getTable();
        return new BufferedDataTable[]{out};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO Code executed on reset.
        // Models build during execute are cleared here.
        // Also data handled in load/saveInternals will be erased here.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
        
        // TODO: check if user settings are available, fit to the incoming
        // table structure, and the incoming types are feasible for the node
        // to execute. If the node can execute in its current state return
        // the spec of its output data table(s) (if you can, otherwise an array
        // with null elements), or throw an exception with a useful user message

        return new DataTableSpec[]{null};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {

        // TODO save user settings to the config object.
        
        m_count.saveSettingsTo(settings);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            
        // TODO load (valid) settings from the config object.
        // It can be safely assumed that the settings are valided by the 
        // method below.
        
        m_count.loadSettingsFrom(settings);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            
        // TODO check if the settings could be applied to our model
        // e.g. if the count is in a certain range (which is ensured by the
        // SettingsModel).
        // Do not actually set any values of any member variables.

        m_count.validateSettings(settings);

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        
        // TODO load internal data. 
        // Everything handed to output ports is loaded automatically (data
        // returned by the execute method, models loaded in loadModelContent,
        // and user settings set through loadSettingsFrom - is all taken care 
        // of). Load here only the other internals that need to be restored
        // (e.g. data used by the views).

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
       
        // TODO save internal models. 
        // Everything written to output ports is saved automatically (data
        // returned by the execute method, models saved in the saveModelContent,
        // and user settings saved through saveSettingsTo - is all taken care 
        // of). Save here only the other internals that need to be preserved
        // (e.g. data used by the views).

    }

}

