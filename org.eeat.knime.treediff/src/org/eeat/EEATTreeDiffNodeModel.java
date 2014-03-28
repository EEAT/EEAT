package org.eeat;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.knime.base.node.mine.decisiontree2.PMMLDecisionTreeTranslator;
import org.knime.base.node.mine.decisiontree2.model.DecisionTree;
import org.knime.base.node.mine.decisiontree2.model.DecisionTreeNode;
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
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.pmml.PMMLPortObject;
import org.knime.core.node.port.pmml.PMMLPortObjectSpec;
import org.knime.core.pmml.PMMLModelType;
import org.w3c.dom.Node;





/**
 * This is the model implementation of EEATTreeDiff.
 * 
 *
 * @author EEAT
 */
public class EEATTreeDiffNodeModel extends NodeModel {
	
	//add port info//
	/** index of input model 1 (=first decisiontree) port. */
	public static final int INModelPORT1 = 0;
	/** index of input model 2 (=first decisiontree) port. */
	public static final int INModelPORT2 = 1;
	
	
    
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(EEATTreeDiffNodeModel.class);
    
    /** Decision Tree
     
     */
       public DecisionTree m_decTree1; //first model
       public DecisionTree m_decTree2; // second model
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
        new SettingsModelIntegerBounded(EEATTreeDiffNodeModel.CFGKEY_COUNT,
                    EEATTreeDiffNodeModel.DEFAULT_COUNT,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);
    

    /**
     * Constructor for the node model.
     */
   public EEATTreeDiffNodeModel() {
    
        // TODO one incoming port and one outgoing port is assumed
        super(new PortType[]{PMMLPortObject.TYPE, PMMLPortObject.TYPE},new PortType[]{BufferedDataTable.TYPE});
        m_decTree1=null;
        m_decTree2=null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inPorts,
            final ExecutionContext exec) throws Exception {
    	
    	//read decision trees//   	
    	// originally was PMMLDecisionTreePortObject, but it's been removed
//    	m_decTree1=((PMMLDecisionTreePortObject)inPorts[INModelPORT1]).getTree();
//    	m_decTree2=((PMMLDecisionTreePortObject)inPorts[INModelPORT2]).getTree();
    	
    	// Code sample from org.knime.base.node.mine.decisiontree2.predictor.DecTreePredictorNodeModel    	 
    	PMMLPortObject port1 = (PMMLPortObject)inPorts[INModelPORT1];
    	PMMLPortObject port2 = (PMMLPortObject)inPorts[INModelPORT2];

        List<Node> models1 = port1.getPMMLValue().getModels(
                PMMLModelType.TreeModel);
        List<Node> models2 = port2.getPMMLValue().getModels(
                PMMLModelType.TreeModel);
        if (models1.isEmpty() || models2.isEmpty()) {
            String msg = "Decision Tree evaluation failed: "
                   + "No tree model found.";
            throw new RuntimeException(msg);
        }
    	
        PMMLDecisionTreeTranslator trans = new PMMLDecisionTreeTranslator();
        port1.initializeModelTranslator(trans);
        m_decTree1 = trans.getDecisionTree();
        
        port2.initializeModelTranslator(trans);
        m_decTree2 = trans.getDecisionTree();


    	// end of reading decision tree//
    	
    	
        // TODO do something here
        logger.info("Node Model Stub... this is not yet implemented !");
        
        
        // process input inData
        exec.setMessage("reading decision trees..");
        assert m_decTree1 !=null;
        DecisionTreeNode root1=m_decTree1.getRootNode();
        DecisionTreeNode root2=m_decTree2.getRootNode();
        
        
        LinkedList<DecisionTreeNode> rootList=new LinkedList();
        rootList.add(root1);
        rootList.add(root2);
        
        // do tree diff work here
        
        
        // output:
        
        // the data table spec of the single output table, 
        // the table will have three columns:
        
        
        DataColumnSpec[] allColSpecs = new DataColumnSpec[6];
        allColSpecs[0] = 
            new DataColumnSpecCreator("Tree Index", IntCell.TYPE).createSpec();
        allColSpecs[1] = 
            new DataColumnSpecCreator("First Attribute name", StringCell.TYPE).createSpec();
        allColSpecs[2] = 
            new DataColumnSpecCreator("First Attribute Importance", DoubleCell.TYPE).createSpec();
        allColSpecs[3] = 
            new DataColumnSpecCreator("Second Attribute name", StringCell.TYPE).createSpec();
        allColSpecs[4] = 
            new DataColumnSpecCreator("Second Attribute Importance", DoubleCell.TYPE).createSpec();
        allColSpecs[5] = 
            new DataColumnSpecCreator("difference description", StringCell.TYPE).createSpec();

        
        
        DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
        // the execution context will provide us with storage capacity, in this
        // case a data container to which we will add rows sequentially
        // Note, this container can also handle arbitrary big data tables, it
        // will buffer to disc if necessary.
        BufferedDataContainer container = exec.createDataContainer(outputSpec);
        // let's add m_count rows to it
        //for (int i = 0; i < m_count.getIntValue(); i++) {
        
      
       
       // BufferedDataTable table = inData[0];
        
        for (int i=0;i<2;i++) {
        	
            // the cells of the current row, the types of the cells must match
            // the column spec (see above)
        	
        	// read cells from input row
          /*  StringCell cell0 = (StringCell) inRow.getCell(0);
            StringCell cell1 = (StringCell) inRow.getCell(1);
            StringCell cell2 = (StringCell) inRow.getCell(2);*/

            // convert to expected types
         /*   Double dblValue = Double.parseDouble(cell0.getStringValue()); 
            int intValue = Integer.parseInt(cell1.getStringValue()) * 2;
            String strValue = cell2.getStringValue() + " foo bar"; */ 
        	
        	
             int intValue = 1;
             String strValue1 = " ";
             String strValue2 = " ";
             double importance1=0.00;
             double importance2=0.00;
            
             DecisionTreeNode currentRoot=rootList.poll();
             LinkedList<String> diff=new LinkedList();//diff description
            
           if(currentRoot.isLeaf())
           {
        	   System.out.println("this root is a leaf");
        	 //  strValue =strValue+"root is leaf";
        	   //add stuff here to deal with the condition of leaf
        	  
        		   
           }
           else
           {
        	   //	strValue =strValue+"root is not leaf";
        	   
        	    double improvement=improvement(currentRoot);
        	   // strValue=strValue+"im of root is "+ Double.toString(improvement);
        	    
        	    String prefix=currentRoot.getPrefix();
    			String attribute=attributeExtraction(prefix);
    			
    			LinkedList<Double> importanceRecord=new LinkedList(); //for this tree, record all its importance for each node
    			LinkedList<String> attRecord=new LinkedList();//for this tree, record all its attribute for each node
    			
    			
    			
    			
    			
    			
    			LinkedList<DecisionTreeNode> nodeList=new LinkedList();
    			LinkedList<Integer> treeMatrix = new LinkedList();
    			LinkedList<DecisionTreeNode> nodeList2=new LinkedList();
    			LinkedList<Integer> treeMatrix2 = new LinkedList();
    			
    			levelOrder(root1,  nodeList,  treeMatrix);
    			levelOrder(root2,  nodeList2,  treeMatrix2);
    			
    			
    			boolean different=treeDiff(nodeList,nodeList2,diff);
    			
    			
    			depth(currentRoot,importanceRecord,attRecord);
    			//fake
    			
    			while(!importanceRecord.isEmpty())
    			{
    				importance1=importance1+importanceRecord.poll();
    			}
    			//fake end
    			//process the list of att and importance here
    		//	strValue=strValue+"length of the importance record is "+importanceRecord.size();
    			//strValue=strValue+"length of the att record is "+attRecord.size();
    			while(!attRecord.isEmpty())
    			{
    				//strValue=strValue+" "+Double.toString(importanceRecord.poll());
    				strValue1=strValue1+" "+attRecord.poll();
    				
    			}
        		/*int count=root1.getChildCount();
        		
        		for(int j=0;j<count;j++)
        		{
        			DecisionTreeNode node=root1.getChildAt(j);
        			
        			String prefix=node.getPrefix();
        			//strValue=strValue+"prefix: "+prefix;
        			
        			String childIndex=Integer.toString(j);
        			String attribute=attributeExtraction(prefix);
        			strValue=strValue+"chilren index: "+childIndex+" attribute: "+attribute;
        			
        			double gini = gini(node);
        			strValue=strValue+"gini vale: "+Double.toString(gini);
        			
        		}*/
        	   
           }
           
        	
          
             

            // create new cells for output
            DataCell[] cells = new DataCell[6];
           
            cells[0] = new IntCell(intValue);
            cells[1] = new StringCell(strValue1); 
            cells[2] = new DoubleCell(importance1);
            cells[3] = new StringCell("null"); 
            cells[4] = new DoubleCell(importance2);
            if(i==0)
            {
            	cells[5]=new StringCell(" ");
            }
            else
            {
            	String differenceDescription=" ";
            	while(!diff.isEmpty())
            	{
            		differenceDescription=differenceDescription+diff.poll();
            	}
            	cells[5]=new StringCell(differenceDescription);
            }
            // create row for output, add to output table
            RowKey key = new RowKey("Row " + i);
            DataRow row = new DefaultRow(key, cells);
            container.addRowToTable(row);
            // check if the execution monitor was canceled
            exec.checkCanceled();
            exec.setProgress(i / (double)m_count.getIntValue(), 
                "Adding row " + i);
            
        
        
        }
        // once we are done, we close the container and return its table
        container.close();
        BufferedDataTable out = container.getTable();
        return new BufferedDataTable[]{out};
    }

    
    public void levelOrder(DecisionTreeNode root, LinkedList<DecisionTreeNode> nodeList, LinkedList<Integer> treeMatrix)
    {
    	System.out.println("come to my levelOrder");
    	
    	
    	int level =0;
    	if(root.isLeaf()){
    		
    		System.out.println("leaf");
    	}
    	else
    	{
    	LinkedList<DecisionTreeNode> bufferNodeList1=new LinkedList();
    	LinkedList<DecisionTreeNode> bufferNodeList2=new LinkedList();
    	
          for(int i=0;i<root.getChildCount();i++)
          {
        	  bufferNodeList1.add(root.getChildAt(i));
          }
          int size=bufferNodeList1.size();
          treeMatrix.add(size);
          System.out.println("size for first level"+treeMatrix.peek());
          
          DecisionTreeNode arrayInitial[]=new DecisionTreeNode[size];
    	  for(int x=0;x<size;x++)
    	  {
    		  arrayInitial[x]=bufferNodeList1.poll();
    		 
    	  }
    	  System.out.println("come here");
    	  bubblesort(arrayInitial);
    	  for(int x=0;x<size;x++)
    	  {
    		  bufferNodeList1.add(arrayInitial[x]);
    	  }
          while(!bufferNodeList1.isEmpty()||!bufferNodeList2.isEmpty())
          {
        	  while(!bufferNodeList1.isEmpty())
        	  {
        		  DecisionTreeNode node=bufferNodeList1.poll();
        		  nodeList.add(node);
        		  int childSize=node.getChildCount();
        		  for(int j=0;j<childSize;j++)
        		  {
        			  bufferNodeList2.add(node.getChildAt(j));
        		  }
        	  }
        	  int levelSize=bufferNodeList2.size();
        	  treeMatrix.add(levelSize);
        	  DecisionTreeNode array[]=new DecisionTreeNode[levelSize];
        	  for(int x=0;x<levelSize;x++)
        	  {
        		  array[x]=bufferNodeList2.poll();
        		  bubblesort(array);
        	  }
        	  for(int x=0;x<levelSize;x++)
        	  {
        		  bufferNodeList2.add(array[x]);
        	  }
        	  while(!bufferNodeList2.isEmpty())	 
        	  {
        		  bufferNodeList1.add(bufferNodeList2.poll());
        	  }
        	 
        	  
        	  
        		  
          }
          
    	
    	}
    	System.out.println("matix"+treeMatrix.size());
    	System.out.println("nodeList"+nodeList.size());
    }
    
    public void depth(DecisionTreeNode node, LinkedList<Double> importanceRecord, LinkedList<String> attRecord)
{	
	
			
	
		double importance=improvement(node)*node.getEntireClassCount();
		String attribute = "";
		
		importanceRecord.add(importance);
		
		for(int i=0;i<node.getChildCount();i++)
		 {
    		DecisionTreeNode nodeChild=node.getChildAt(i);
    	
    		
    		if(nodeChild.isLeaf())
           {
        	
        	System.out.println("leaf:");
        	System.out.println("att of this leaf is:"+nodeChild.getPrefix());
        	
        	attribute = attributeExtraction( nodeChild.getPrefix());
        	int attRecordSize=attRecord.size();
        	int counter=0;
        	while(counter<attRecordSize)
        	{
        		if(attribute.equalsIgnoreCase(attRecord.get(counter)))
        			break;
        		else
        			counter++;
        		
        	}
        	if(counter==attRecordSize)
        		attRecord.add(attribute);
    		
        	double gini=0;
        	gini=gini(nodeChild);
        	System.out.println("gini index for this leaf node is"+gini);
        	
        	double improvement=improvement(nodeChild);
        	
        	System.out.println("improvment for this leaf node is:"+ improvement);
           }
           else
           {
        	
           	double gini=0;
           	gini=gini(nodeChild);
           	
           	attribute = attributeExtraction( nodeChild.getPrefix());
    		attRecord.add(attribute);
        	
        	double improvement=improvement(nodeChild);
        	
        	importance =improvement*nodeChild.getEntireClassCount();
        	
           	depth(nodeChild,importanceRecord,attRecord);
           	
        
           }
    	    		
    	}
	}

    
    		void eventCount(DecisionTreeNode node, LinkedList <Double> eventCount)
     {
    		
    		LinkedHashMap map = node.getClassCounts();
			Collection collection=map.values();
			Iterator interator=collection.iterator();
			
			while(interator.hasNext())
			{
				eventCount.add((Double) interator.next());
				
			}
    	
     }
     public String attributeExtraction(String decisionRule)// return the attribute
     {
    	 String attribute="";
    	 if(decisionRule.contains("<"))
    	 {
    	 int index=decisionRule.indexOf("<");
    	
    	 attribute=attribute.concat(decisionRule.substring(0, index));
    	 }
    	 else if(decisionRule.contains(">"))
    			 {
    		 int index=decisionRule.indexOf(">");
        	 attribute=attribute+decisionRule.substring(0, index);
    			 }
    	 else if(decisionRule.contains("="))
		 {
    		 int index=decisionRule.indexOf("=");
    		 attribute=attribute+decisionRule.substring(0, index);
		 }
    	 return attribute;
     }
     public String conditionExtraction(String decisionRule)
     {
    	 String condition="";
    	 
    	 if(decisionRule.contains("="))
		 {
    		 int index=decisionRule.indexOf("=");
    		 condition=condition+decisionRule.substring(index+2, decisionRule.length());
		 }
    	 if(condition.equalsIgnoreCase("email"))
    		 condition="1";
    	 else if(condition.equalsIgnoreCase("read"))
    		 condition="2";
    	 else
    		 condition="3";
    	 
    	 return condition;
     }
     
     public String valueExtraction(String decisionRule)
     {
    	 String value="";
    	 System.out.println("test"+value);
    	 return value;
    	
    	 
     }
     public double gini(DecisionTreeNode node)// return gini index for a given node
		{

	          	LinkedList <Double> eventCountList=new LinkedList();// to include all the event counts for a given node
       			eventCount(node, eventCountList);
       			
	        	double gini=0;
	        	double sum=node.getEntireClassCount();
	        	while(!eventCountList.isEmpty())
	        	{
	        	
	        		
	        		double countCount=eventCountList.poll();
	        		
	        	
	        		double p=countCount/sum;
	        		
	        		gini=gini+p*(1-p);
	        	}
	        	
	        	return gini;         
	          
		}
     
     	public double improvement(DecisionTreeNode node) // need to check the fomula of the improvement
		{
			double improvement=gini(node);
			
			
			if(node.isLeaf())
			{
				return 0;
			}
			else
			{
				double sumEvent=node.getEntireClassCount();
				double temp=0;
				for(int i=0;i<node.getChildCount();i++)
				 {
					DecisionTreeNode child=node.getChildAt(i);
					double giniChild=gini(child);
					double ratio=child.getEntireClassCount();// need to check the fomula
					double result=ratio*giniChild;
					result=result/sumEvent;
					temp=temp+result;
						
				}
				improvement=improvement-temp;
				
			}
			return improvement;
		}
     	public void bubblesort(DecisionTreeNode array[]) {
    		
    		int length = array.length;
    		
    		for (int i = 0; i < length; i++) {
    			for (int j = 0; j < length - 1; j++) {
    				
    				if (compare(array[j], array[j + 1])) {
    					//System.out.println("now need to reverse");

    					//swap(array[j], array[j + 1]);
    					DecisionTreeNode temp;
    					temp=array[j];
    					array[j]=array[j+1];
    					array[j+1]=temp;
    					
    					//System.out.println("after swap, now j is"+conditionExtraction(array[j].getPrefix()));
    					//System.out.println("after swap, now j+1 is"+conditionExtraction(array[j+1].getPrefix()));
    				}

    			}
    		}
    	}
     	public boolean compare(DecisionTreeNode a, DecisionTreeNode b) {
    		
    		String sa = conditionExtraction(a.getPrefix());
    		String sb = conditionExtraction(b.getPrefix());
    		
    		int conditionA=Integer.parseInt(sa);
    		int conditionB=Integer.parseInt(sb);
    
    		if(conditionA>conditionB)
    			return true;
    		else
    			return false;
    	}
     	public boolean treeDiff(LinkedList<DecisionTreeNode> arraya, LinkedList<DecisionTreeNode> arrayb,
    			LinkedList<String> DiffColumn){
    		
    	
    		LinkedList<DecisionTreeNode> arrayRecorder = new LinkedList();
            
    		if(arraya.isEmpty()&&arrayb.isEmpty())
    		{
    			System.out.println("both empaty");
    			return false;
    		}
    		if(!arraya.isEmpty()&&arrayb.isEmpty())
    		{
    			System.out.println("both empaty");
    			return false;
    		}
    		int index=0;
    		DecisionTreeNode a = arraya.peek();
			DecisionTreeNode b = arrayb.peek();
    		while (!arraya.isEmpty() && !arrayb.isEmpty()) {

    			
    			
    			
    			index++;
    			String attributeA=attributeExtraction(a.getPrefix());//get attribute of the  node in treeA
    			String attributeB=attributeExtraction(b.getPrefix());//get attribute of the  node in treeB
    			
    			if (attributeA.compareTo(attributeB) != 0) {
    				System.out.println("attribute is different:");
    				
    				DiffColumn.add("attribute");
    				System.out.println("expected "
    						+ attributeA + "; but it is "
    						+ attributeB);
    			
    				
    				DiffColumn.add(attributeA);
    				DiffColumn.add(attributeB);
    				return true;
    			}
    			
    			
    			
    			
    				
    				String valueA=conditionExtraction(a.getPrefix());
        			String valueB=conditionExtraction(b.getPrefix());
        			int valueAInt=Integer.parseInt(valueA);
        			int valueBInt = Integer.parseInt(valueB);
        			if (valueAInt>valueBInt) {
    				System.out.println("value is different");
    				
    			//	DiffColumn.add("value");
    				
    				System.out.println("Node "+a.getOwnIndex() + ":" + "expected " + " "
    						+ valueAInt + "; but it is "
    						+ valueBInt);
    				
    				
    				DiffColumn.add("increase activity at"+valueAInt);
    				
    				System.out.println("The path until this point in depth order is: ");	
    				
    			
    				b=arrayb.poll();	
    			}
    			else if (valueAInt<valueBInt) {
    				System.out.println("value is different");
    				
    				//DiffColumn.add("value");
    				DiffColumn.add("decrease activity at"+valueAInt);
    				
    				
    				System.out.println("Node "+a.getOwnIndex() + ":" + "expected " + " "
    						+ valueAInt + "; but it is "
    						+ valueBInt);
    				
    				
    				//DiffColumn.add(valueA);
    				//DiffColumn.add(valueB);
    				// System.out.println("The path until this point in breadth order is: ");
    				// printPathBreadth(arrayRecorder);

    				System.out
    						.println("The path until this point in depth order is: ");	
    				a=arraya.poll();
    				
    			}
    			else
    			{
    				System.out.println("same");
    				a=arraya.poll();
    				b=arrayb.poll();
    			}
        			a=arraya.peek();
    				b=arrayb.peek();
    				
        		}
    			
    			if(arraya.isEmpty()&&arrayb.isEmpty())
				{
					System.out.println("tree or subtress of both A and B are empty. ");
					DiffColumn.add("tree or subtress of both A and B are empty. ");
					return true;
				}
				else if(arraya.isEmpty()&&!arrayb.isEmpty())
				{
					System.out.println("tree A is empty now");
					DiffColumn.add("tree A is empty now");
					DecisionTreeNode bnext=arrayb.poll();
					System.out.println("The next different edge of tree B is: "+bnext.getStringSummary());
					DiffColumn.add("The next different edge of tree B is: "+bnext.getStringSummary());
					return true;
				}
				else if(arrayb.isEmpty()&&!arraya.isEmpty())
				{
					System.out.println("Tree B is empty now. ");
					DiffColumn.add("tree B is empty now");
					DecisionTreeNode anext=arrayb.poll();
					System.out.println("The first different edge of tree A is: "+anext.getStringSummary());
					DiffColumn.add("The next different edge of tree A is: "+anext.getStringSummary());
				
					return true;
				}
			
				else
				{
					return false;
				}
    		

    		
    	}
public void treeDiff()
     	{
     		
     	}
     	
     	
     
     	
     	
     	public void swap(DecisionTreeNode a,  DecisionTreeNode b)// is not used since swapping outside array is not working
    	{
    		//System.out.println("come to swap");
    		
    		DecisionTreeNode c;
    		c=a;
    		a=b;
    		b=c;
    		String sa = conditionExtraction(a.getPrefix());
    		String sb = conditionExtraction(b.getPrefix());
    		
    		System.out.println("new sa is"+sa);
    		System.out.println("new sb is"+sb);
    		
    		
    		
    	

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
  /*  protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
        
        // TODO: check if user settings are available, fit to the incoming
        // table structure, and the incoming types are feasible for the node
        // to execute. If the node can execute in its current state return
        // the spec of its output data table(s) (if you can, otherwise an array
        // with null elements), or throw an exception with a useful user message

        return new DataTableSpec[]{null};
    }*/
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
    throws InvalidSettingsException {
PMMLPortObjectSpec treeSpec1 = (PMMLPortObjectSpec)inSpecs[INModelPORT1];
//DataTableSpec inSpec = (DataTableSpec)inSpecs[1];
PMMLPortObjectSpec treeSpec2 = (PMMLPortObjectSpec)inSpecs[INModelPORT2];
/*for (String learnColName : treeSpec1.getLearningFields()) {
   // if (!inSpec.containsName(learnColName)) {
	 	if(true){
        throw new InvalidSettingsException(
                "Learning column \"" + learnColName
                + "\" not found in input "
                + "data to be predicted");
    }
}*/
DataColumnSpec[] allColSpecs = new DataColumnSpec[6];
allColSpecs[0] = 
    new DataColumnSpecCreator("Tree Index", IntCell.TYPE).createSpec();
allColSpecs[1] = 
    new DataColumnSpecCreator("First Attribute name", StringCell.TYPE).createSpec();
allColSpecs[2] = 
    new DataColumnSpecCreator("First Attribute Importance", DoubleCell.TYPE).createSpec();
allColSpecs[3] = 
    new DataColumnSpecCreator("Second Attribute name", StringCell.TYPE).createSpec();
allColSpecs[4] = 
    new DataColumnSpecCreator("Second Attribute Importance", DoubleCell.TYPE).createSpec();
allColSpecs[5] = 
    new DataColumnSpecCreator("difference description", StringCell.TYPE).createSpec();

DataTableSpec outputSpec = new DataTableSpec(allColSpecs);

return new PortObjectSpec[]{
		outputSpec};
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

