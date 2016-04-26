/*
*
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%    ___  ___  ____  ___  ______________
%   / _ \/ _ \/ __ \/ _ )/ __/  _/_  __/
%  / ___/ , _/ /_/ / _  / _/_/ /  / /   
% /_/  /_/|_|\____/____/_/ /___/ /_/    
%                                      
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
*
*/

package sandip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


public class SandipGrammarInduction {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		String hier_file_path = args[0];
		String dist_file_path = args[1];
		
		
		List<Scene> scenes = parseFiles(hier_file_path, dist_file_path);
		
		
		// For each scene
			//build tree
		
		
		//For each tree_i
			//for every other tree_j
				//match(tree_i, tree_j)

		// parseKMeansResult();
		
		System.out.println();

	}
	
	
	static List<Scene> parseFiles(String hier_file_path, String dist_file_path) throws IOException 
	{
		File folder = new File(hier_file_path);
		File[] listOfFiles = folder.listFiles();
		
		//uses- will decide later
		List<Scene> scenes = new ArrayList<>();
//		Map<String, Scene> scenes_map = new HashMap<>();

		for (int i = 0; i < listOfFiles.length; i++)
			
			if (listOfFiles[i].isFile()) {
				
//				System.out.println("Reading File- " + listOfFiles[i].getName());

				//files to parse
				String hier_file = hier_file_path + "/" + listOfFiles[i].getName();
				String dist_file = dist_file_path + "/" + listOfFiles[i].getName();
				dist_file =dist_file.replace(".hier", ".dist");

				Scene s1 = new Scene();
				s1.parseScene(hier_file, dist_file);
				s1.buildSceneTree();
				
				
				//Add to list/map - uses will decide later
				scenes.add(s1);
//				scenes_map.put(s1.name, s1);
				
				
//				System.out.println("----------------------------------------------------------------------------");
//				System.out.println(s1);
			}

//		scenes.get(0).sct.compareTree(scenes.get(1).sct);
		
		for(int i =0; i<scenes.size();i++){
			for(int j =i+1; j<scenes.size();j++){
				System.out.println(scenes.get(i).name + " - " + scenes.get(j).name);
				scenes.get(i).sct.compareTree(scenes.get(j).sct);
				System.out.println("----------------------------------------------------------------------------");
			}
		}
		
		
//		 System.out.println("Reading files done...");
		 
		 return scenes;
	}
	
	
	/**
	 * 
	 * @throws IOException
	 */
	public static void parseKMeansResult() throws IOException {
		List<String> labels = new ArrayList<>();
		List<Integer> grps = new ArrayList<>();
		SortedMap<Integer, Map<String, Integer>> mp = new TreeMap<>();
		String str;

		BufferedReader br = new BufferedReader(new FileReader(new File(
				"java_ip_labels.txt")));
		while ((str = br.readLine()) != null)
			labels.add(str);
		br.close();

		br = new BufferedReader(new FileReader(new File("k_means_result.txt")));
		while ((str = br.readLine()) != null)
			grps.add(Integer.parseInt(str));
		br.close();

		for (int i = 0; i < grps.size(); i++) {

			if (!mp.containsKey(grps.get(i)))
				mp.put(grps.get(i), new HashMap<String, Integer>());

			Map<String, Integer> cnt_map = mp.get(grps.get(i));
			if (!cnt_map.containsKey(labels.get(i)))
				cnt_map.put(labels.get(i), 0);

			cnt_map.put(labels.get(i), cnt_map.get(labels.get(i)) + 1);
			mp.put(grps.get(i), cnt_map);
		}

		for (Integer i : mp.keySet()) {
			System.out.println("--------- Group - " + i + " ---------");
			Map<String, Integer> cnt_map = mp.get(i);
			for (String s : cnt_map.keySet()) {
				System.out.println(s + " - " + cnt_map.get(s));
			}

		}

		System.out.println();
	}
}
