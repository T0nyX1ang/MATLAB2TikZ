import java.io.*;

public class CommandExecutor {

	public void execute(String command, String directory) {
		boolean err = false;
		try {
			// Initializing ProcessBuilder object.
			String[] command_list = command.split(" ");
			ProcessBuilder pb = new ProcessBuilder(command_list);
			// change to canonical directory.
			pb.directory(new File(directory).getAbsoluteFile());
			Process p = pb.start();
			
			// Store result messages while executing.
			BufferedReader results = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s;
			while((s = results.readLine()) != null){
				System.out.println(s);
			}
			
			// Store error messages while executing.
			BufferedReader errors = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while((s = errors.readLine()) != null){
				System.err.println(s);
				err = true;
			}
			
			// Destroy the process forcibly.
			p.destroyForcibly();
		} catch (FileNotFoundException fnfe) {
			System.err.println("File not found. " + fnfe);
		} catch (IOException e) {
			System.err.println("Unknown error. " + e);
		}
		if (err) {
			throw new RuntimeException("Error executing: "+ command);
		} else {
			System.out.println("Successfully executing: " + command);
		}
	}
	
}
