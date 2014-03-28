package org.eeat.knime.google.bigquery.node;

import java.io.File;
import java.io.IOException;

//import org.knime.base.node.mine.decisiontree2.PMMLDecisionTreePortObject;
// This is no longer available in knime-base.jar. It does not seem to be used. So I commented it out.
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
import org.knime.core.node.port.PortType;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.Bigquery.Datasets;
import com.google.api.services.bigquery.Bigquery.Jobs.Insert;
import com.google.api.services.bigquery.model.DatasetList;
import com.google.api.services.bigquery.model.GetQueryResultsResponse;
import com.google.api.services.bigquery.model.Job;
import com.google.api.services.bigquery.model.JobConfiguration;
import com.google.api.services.bigquery.model.JobConfigurationQuery;
import com.google.api.services.bigquery.model.JobReference;
import com.google.api.services.bigquery.model.TableCell;
import com.google.api.services.bigquery.model.TableRow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
/**
 * This is the model implementation of BigQuery.
 * Node for reading from bigquery
 *
 * @author 
 */
public class MyBigQueryNodeModel extends NodeModel {
    
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(MyBigQueryNodeModel.class);
        
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
        new SettingsModelIntegerBounded(MyBigQueryNodeModel.CFGKEY_COUNT,
        		MyBigQueryNodeModel.DEFAULT_COUNT,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);
   //google related start
    private static final String PROJECT_ID = "703902485613";
    private static final String CLIENTSECRETS_LOCATION = "client_secrets.json";
   
    static GoogleClientSecrets clientSecrets = loadClientSecrets();
    //static GoogleClientSecrets clientSecrets = null;

    // Static variables for API scope, callback URI, and HTTP/JSON functions
    private static final List<String> SCOPES = Arrays.asList(
        "https://www.googleapis.com/auth/bigquery");
    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
    private static final HttpTransport TRANSPORT = new NetHttpTransport();
    
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    
    private static GoogleAuthorizationCodeFlow flow = null;
    //google related end
    /**
     * Constructor for the node model.
     */
    protected MyBigQueryNodeModel() {
    
        // TODO one incoming port and one outgoing port is assumed
       // super(0, 1);
        super(new PortType[]{BufferedDataTable.TYPE},new PortType[]{BufferedDataTable.TYPE});
         
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {

        // TODO do something here
        logger.info("Node Model Stub... this is not yet implemented !");
       //read the table
        
      //  m_decTree1=((PMMLDecisionTreePortObject)inPorts[INModelPORT1]).getTree();
       
        BufferedDataTable table = inData[0];
        int rowCount = table.getRowCount();
        System.out.println("rowcount is"+rowCount); 
        String query="";
        int currentRow = 0;
        for (DataRow row : table) {
        	  for (int i = 0; i < row.getNumCells(); i++) {
                  DataCell cell = row.getCell(i);
                  query=cell.toString() ;
        	  }
        }
        System.out.println("query is"+query);
        ///the main function
        // Create a new BigQuery client authorized via OAuth 2.0 protocol
        
         
        
        
        Bigquery bigquery = createAuthorizedClient();

        // Print out available datasets to the console
       listDatasets(bigquery, "publicdata");

        // Start a Query Job
        //String querySql = "SELECT TOP(word, 50), COUNT(*) FROM publicdata:samples.shakespeare";
       // JTextArea text = new JTextArea("please type in your sql");
       
        //String querySql=JOptionPane.showInputDialog(text);
       String querySql=query;
       System.out.println("querySql is"+querySql);
        
        JobReference jobId = startQuery(bigquery, PROJECT_ID, querySql);

        // Poll for Query Results, return result output
        Job completedJob = checkQueryResults(bigquery, PROJECT_ID, jobId);

        // Return and display the results of the Query Job
        GetQueryResultsResponse queryResult=displayQueryResults(bigquery, PROJECT_ID, completedJob);
        //////end of main function
        
        List<TableRow> rows = queryResult.getRows();
        TableRow firstrow = rows.get(0);
       
        int columnNumber=firstrow.getF().size();
        System.out.println("columnNumber is"+columnNumber);
        // the data table spec of the single output table, 
        // \
        DataColumnSpec[] allColSpecs = new DataColumnSpec[columnNumber];
        /*allColSpecs[0] = 
            new DataColumnSpecCreator("Column 0_tj", StringCell.TYPE).createSpec();
        allColSpecs[1] = 
            new DataColumnSpecCreator("Column 1", DoubleCell.TYPE).createSpec();
        allColSpecs[2] = 
            new DataColumnSpecCreator("Column 2", IntCell.TYPE).createSpec();*/
        for(int m=0;m<columnNumber;m++)
        {
        	allColSpecs[m] = 
                new DataColumnSpecCreator("Column"+m, StringCell.TYPE).createSpec();
        }
        DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
        // the execution context will provide us with storage capacity, in this
        // case a data container to which we will add rows sequentially
        // Note, this container can also handle arbitrary big data tables, it
        // will buffer to disc if necessary.
        BufferedDataContainer container = exec.createDataContainer(outputSpec);
        // let's add m_count rows to it
        /*for (int i = 0; i < m_count.getIntValue(); i++) {
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
        int i=0;
        for ( TableRow row : rows) {
        	RowKey key = new RowKey("Row " + i);
        	List<TableCell> cells = row.getF();
        	int size=cells.size();
        	DataCell[] newcells = new DataCell[size];
        	for(int j=0;j<size;j++)
        	{
        		newcells[j]=new StringCell(cells.get(j).getV().toString());
        	}
        	DataRow newrow = new DefaultRow(key, newcells);	
        	container.addRowToTable(newrow);
            //DataRow row = new DefaultRow(key, cells);
           
       
            
            // check if the execution monitor was canceled
            exec.checkCanceled();
            exec.setProgress(i / (double)m_count.getIntValue(), 
                "Adding row " + i);
            i++;
            
            //System.out.println();
          }
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
    
    
    ///functions for the google part
    /**
     * Creates an authorized BigQuery client service using the OAuth 2.0 protocol
     * <p>
     * This method first creates a BigQuery authorization URL, then prompts the
     * user to visit this URL in a web browser to authorize access. The
     * application will wait for the user to paste the resulting authorization
     * code at the command line prompt.
     * @return 
     *
     * @return an authorized BigQuery client
     * @throws IOException
     */
    	public static Bigquery createAuthorizedClient() throws IOException {

        String authorizeUrl = new GoogleAuthorizationCodeRequestUrl(
            clientSecrets,
            REDIRECT_URI,
            SCOPES).setState("").build();

        System.out.println("Paste this URL into a web browser to authorize BigQuery Access:\n" + authorizeUrl);
        String output="Paste this URL into a web browser to authorize BigQuery Access:\n" + authorizeUrl;
       // JOptionPane.showMessageDialog(null,output);
        
        System.out.println("... and type the code you received here: ");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
      
        
        //String authorizationCode = in.readLine();
        JTextArea text = new JTextArea(output.toString());
        JOptionPane.showMessageDialog(null,text);
        String authorizationCode=JOptionPane.showInputDialog(text);
        System.out.println(authorizationCode);
        
        // Exchange the auth code for an access token and refesh token
        Credential credential = exchangeCode(authorizationCode);
        

        return Bigquery.builder(TRANSPORT, JSON_FACTORY)
            .setHttpRequestInitializer(credential)
            .setApplicationName("Your User Agent Here")
            .build();
      }
    
    /**
     * Display all BigQuery Datasets associated with a Project
     *
     * @param bigquery  an authorized BigQuery client
     * @param projectId a string containing the current project ID
     * @throws IOException
     */
    public static void listDatasets(Bigquery bigquery, String projectId)
        throws IOException {
      Datasets.List datasetRequest = bigquery.datasets().list(projectId);
      DatasetList datasetList = datasetRequest.execute();
      if (datasetList.getDatasets() != null) {
        List<DatasetList.Datasets> datasets = datasetList.getDatasets();
        System.out.println("Available datasets\n----------------");
        System.out.println(datasets.toString());
        for (DatasetList.Datasets dataset : datasets) {
          System.out.format("%s\n", dataset.getDatasetReference().getDatasetId());
        }
      }
    }
    /**
     * Creates a Query Job for a particular query on a dataset
     *
     * @param bigquery  an authorized BigQuery client
     * @param projectId a String containing the current Project ID
     * @param querySql  the actual query string
     * @return a reference to the inserted query Job
     * @throws IOException
     */
    public static JobReference startQuery(Bigquery bigquery, String projectId,
                                          String querySql) throws IOException {
      System.out.format("\nInserting Query Job: %s\n", querySql);

      Job job = new Job();
      JobConfiguration config = new JobConfiguration();
      JobConfigurationQuery queryConfig = new JobConfigurationQuery();
      config.setQuery(queryConfig);

      job.setConfiguration(config);
      queryConfig.setQuery(querySql);

      Insert insert = bigquery.jobs().insert(projectId, job);
      insert.setProjectId(projectId);
      JobReference jobId = insert.execute().getJobReference();

      System.out.format("\nJob ID of Query Job is: %s\n", jobId.getJobId());

      return jobId;
    }
    
    /**
     * Polls the status of a BigQuery job, returns Job reference if "Done"
     *
     * @param bigquery  an authorized BigQuery client
     * @param projectId a string containing the current project ID
     * @param jobId     a reference to an inserted query Job
     * @return a reference to the completed Job
     * @throws IOException
     * @throws InterruptedException
     */
    private static Job checkQueryResults(Bigquery bigquery, String projectId, JobReference jobId)
        throws IOException, InterruptedException {
      // Variables to keep track of total query time
      long startTime = System.currentTimeMillis();
      long elapsedTime = 0;

      while (true) {
        Job pollJob = bigquery.jobs().get(projectId, jobId.getJobId()).execute();
        elapsedTime = System.currentTimeMillis() - startTime;
        System.out.format("Job status (%dms) %s: %s\n", elapsedTime,
            jobId.getJobId(), pollJob.getStatus().getState());
        if (pollJob.getStatus().getState().equals("DONE")) {
          return pollJob;
        }
        // Pause execution for one second before polling job status again, to
        // reduce unnecessary calls to the BigQUery API and lower overall
        // application bandwidth.
        Thread.sleep(1000);
      }
    }
    
    /**
     * Makes an API call to the BigQuery API
     *
     * @param bigquery     an authorized BigQuery client
     * @param projectId    a string containing the current project ID
     * @param completedJob to the completed Job
     * @throws IOException
     */
    GetQueryResultsResponse displayQueryResults(Bigquery bigquery,
                                            String projectId, Job completedJob) throws IOException {
      GetQueryResultsResponse queryResult = bigquery.jobs()
          .getQueryResults(
              PROJECT_ID, completedJob
              .getJobReference()
              .getJobId()
          ).execute();
      List<TableRow> rows = queryResult.getRows();
      System.out.print("\nQuery Results:\n------------\n");
      for (TableRow row : rows) {
        for (TableCell field : row.getF()) {
          System.out.printf("%-20s", field.getV());
        }
        System.out.println();
      }
      return queryResult;
    }
    
    /**
     * Helper to load client ID/Secret from file.
     *
     * @return a GoogleClientSecrets object based on a clientsecrets.json
     */
    private static GoogleClientSecrets loadClientSecrets() {
      try {
        GoogleClientSecrets clientSecrets =
        	
            GoogleClientSecrets.load(new JacksonFactory(),
                MyBigQueryNodeModel.class.getResourceAsStream(CLIENTSECRETS_LOCATION));
        return clientSecrets;
      } catch (Exception e) {
        System.out.println("Could not load clientsecrets.json");
        e.printStackTrace();
      }
      return clientSecrets;
    }
    
    /**
     * Exchange the authorization code for OAuth 2.0 credentials.
     *
     * @return
     */
    static Credential exchangeCode(String authorizationCode) throws IOException {
      GoogleAuthorizationCodeFlow flow = getFlow();
      GoogleTokenResponse response =
          flow.newTokenRequest(authorizationCode).setRedirectUri(REDIRECT_URI).execute();
      return flow.createAndStoreCredential(response, null);
    }
    /**
     * Build an authorization flow and store it as a static class attribute.
     */
    static GoogleAuthorizationCodeFlow getFlow() {
      if (flow == null) {
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();

        flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport,
            jsonFactory,
            clientSecrets,
            SCOPES)
            .setAccessType("offline").setApprovalPrompt("force").build();
      }
      return flow;
    }

    //end of functions for the google part

}

