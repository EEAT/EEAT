package org.eeat.repository.drools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleCommandReader implements Runnable {
	Repository repository;
	boolean stopNow = false;
	
	public ConsoleCommandReader(Repository repository) {
		this.repository = repository;
	}
	
    public void run() {
        try {
            final InputStreamReader isr = new InputStreamReader( System.in );
            final BufferedReader br = new BufferedReader( isr );
            while( ! stopNow ) {
            	System.out.print("> ");	
                String input = br.readLine();
                try {
					processCommandLine(input);
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    public void stop() {
    	stopNow = true;
    }

	private void processCommandLine(String input) throws Throwable {
//		repository.getLog().debug("Processing console input: " + input);
		if (input.equalsIgnoreCase("(exit)")) {
			stopNow = true;	
		}
		else if (input.equalsIgnoreCase("(reset)")) {
			repository.reset();		
		}	
		else if (input.equalsIgnoreCase("(loadPlugins)")) {
			repository.getComponent().loadPlugins();		
		}	
		else if (input.equalsIgnoreCase("(closeAudit)")) {
			repository.closeAudit();		
		}	
		else  {
			repository.getLog().warn("Unrecognized repository command: " + input);
			repository.getLog().info("Commands: (exit) (reset) (loadPlugins) (closeAudit)");
		}	
	}
}
