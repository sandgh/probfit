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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class BBox{
	
	double x_min,y_min,z_min,x_max,y_max,z_max;
	
	public BBox(double x_min, double y_min, double z_min, double x_max,
			double y_max, double z_max) {
		super();
		this.x_min = x_min;
		this.y_min = y_min;
		this.z_min = z_min;
		this.x_max = x_max;
		this.y_max = y_max;
		this.z_max = z_max;
	}

	List<Double> getBBoxCenter(){
		
		List<Double> bbox_cent = new ArrayList<>();
		bbox_cent.add((x_min+x_max)/2);
		bbox_cent.add((y_min+y_max)/2);
		bbox_cent.add((z_min+z_max)/2);
		
		return bbox_cent;
	}
	
}

class SceneNode{
	int node_index;
	int ground_truth_label;
    String ground_truth_label_name;
	
    int parent;
    boolean is_leaf = false;
    
    List<Double> features = new ArrayList<>();
	
	Map<Integer,Double> distances = new HashMap<>();
	Map<Integer,Double> normalize_distances = new HashMap<>();
	
	BBox node_bbox;
	
	List<SceneNode> children = new ArrayList<>();
}


public class SceneTree {

	SceneNode root = null;
	
	//format <node_id, node> - mapped node_index->node
	Map<Integer, SceneNode> node_map = new HashMap<>();
	
	//these two lists contains the mapped and unmapped nodes while matching 2 trees
	List<Integer> unmapped_list = new ArrayList<>();
	Map<Integer, Integer> mapped_map = new HashMap<>();
	
	//used by the greedy top-down
	Map<Integer, Integer> curr_map = new HashMap<>();
	Map<Integer, Integer> final_map = new HashMap<>();
	
	/**
	 * This will be called before we start matching two trees
	 */
	private void addAllNodesToUnmappedList(){
		
		unmapped_list.clear();
		mapped_map.clear();
		
		for (int idx : node_map.keySet())	unmapped_list.add(idx);
	}
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	public Map<String, Integer> compareTree(SceneTree t){

		Map<String, String> match = new HashMap<>(); 
		
		addAllNodesToUnmappedList();
		t.addAllNodesToUnmappedList();
		
		
//		this.unmapped_list.remove((Object)root.node_index);
//		t.unmapped_list.remove((Object)t.root.node_index);		
//		mapped_map.put(root.node_index, t.root.node_index);
//		t.mapped_map.put(t.root.node_index, this.root.node_index);
//		compareNodesTopDown(this, t, match);
		
//		compareNodesBottomUp(this, t, match);
		
//		for (String str : match.keySet()) {
//			System.out.println(str + " --> " + match.get(str));
//		}
		
		//greedy approach testing
		this.curr_map.clear();
		this.final_map.clear();
		this.curr_map.put(this.root.node_index, t.root.node_index);
		compareNodesTopDown_greedy(this, t, match);
		
//		compareNodesBottomUp(this, t, match);
		
		for (Integer str : final_map.keySet()) {
			System.out.println(this.node_map.get(str).ground_truth_label_name + " --> " + 
										t.node_map.get(final_map.get(str)).ground_truth_label_name 
										+ "(" + this.node_map.get(str).node_index
										 + " - " + t.node_map.get(final_map.get(str)).node_index +")");
		}
		
		return null;
	}
	
	/**
	 * Add a node to the mapped list
	 * @param i - Index of the node in this tree 
	 * @param j - Index of the node to the compared tree
	 */
	private void addToMappedList(int i, int j){
		this.unmapped_list.remove((Object)i);
		this.mapped_map.put(i, j);
	}
	
	/**
	 * Removes a node from mapped list and placed it to the unmapped list
	 * @param i
	 * @param posn
	 */
	private void removeFromMappedList(int i, int posn){
		this.mapped_map.remove(i);
		this.unmapped_list.add(posn, i);
	}
	
	/**
	 * Compute the score of matching
	 * @return
	 */
	private String computeMatchScore(){
		
		int m, r, u;
		m=r=u=0;
		
		for (int i : mapped_map.keySet()) {
			if(i==mapped_map.get(i))	m++;
			else if(mapped_map.get(i)==-1)	u++;
			else	r++;
		}
		
		
		return "m="+ m + ", r=" + r + ", u=" + u;
	}
	
	/**
	 * 
	 * @param t1
	 * @param t2
	 * @param match_map
	 */
	private void compareNodesTopDown(SceneTree t1, SceneTree t2, Map<String, String> match_map){
		
		//base condition
		/** @todo - if one of the tree is completely mapped 
		 *  then the remaining nodes of the other will remain unmapped
		**/
		if(t1.unmapped_list.isEmpty() && t2.unmapped_list.isEmpty()){
			
			StringBuffer sb = new StringBuffer();
			
			for (int i : t1.mapped_map.keySet()) {
				sb.append(i+"->"+t1.mapped_map.get(i) + ", ");
			}
			match_map.put(sb.toString(), computeMatchScore());
			
			sb = new StringBuffer();
			for (int i : t1.mapped_map.keySet()) {
				sb.append(this.node_map.get(i).ground_truth_label_name+"->"+
							t1.node_map.get(this.mapped_map.get(i)).ground_truth_label_name + ", ");
			}
			System.out.println(sb.toString());
		}
		
		
		for (int i=0;i<t1.unmapped_list.size(); i++) {
			
			int v1 = t1.unmapped_list.get(i);
			if(!mapped_map.containsKey(t1.node_map.get(v1).parent))
				continue;
			
			for (int j=0;j<t2.unmapped_list.size(); j++) {
				
				int v2 = t2.unmapped_list.get(j);
				boolean no_match = false;
				
				//matched only nodes that are children of the mapped parents
				if( t2.mapped_map.containsKey(t2.node_map.get(v2).parent)) 
				{
						//just for now, as coe is taking time
					if(!(mapped_map.get(t1.node_map.get(v1).parent) == t2.node_map.get(v2).parent))
						continue;
//						no_match = true;

					t1.addToMappedList(v1, no_match?-1:v2);
					t2.addToMappedList(v2, no_match?-1:v1);
					
					compareNodesTopDown(t1,t2,match_map);
					
					t1.removeFromMappedList(v1,i);
					t2.removeFromMappedList(v2,j);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param t1
	 * @param t2
	 * @param match_map
	 */
	private void compareNodesTopDown_greedy(SceneTree t1, SceneTree t2, Map<String, String> match_map) {
		
		while (!curr_map.isEmpty()) {

			Map<Integer, Integer> next_curr_map = new HashMap<>();
			for (Integer i : curr_map.keySet()) {

				SceneNode v1 = t1.node_map.get(i);
				SceneNode v2 = t2.node_map.get(curr_map.get(i));

				// base
				if (v1.children.size() > 0 && v2.children.size() > 0) {
					double diff1 = calculateNodeDist(v1.children.get(0), v2.children.get(0));
					double diff2 = calculateNodeDist(v1.children.get(1), v2.children.get(1));

					double diff3 = calculateNodeDist(v1.children.get(1), v2.children.get(0));
					double diff4 = calculateNodeDist(v1.children.get(0), v2.children.get(1));

					if ((diff1 + diff2) > (diff3 + diff4)) {
						next_curr_map.put(v1.children.get(1).node_index, v2.children.get(0).node_index);
						next_curr_map.put(v1.children.get(0).node_index, v2.children.get(1).node_index);
					} else {
						next_curr_map.put(v1.children.get(1).node_index, v2.children.get(1).node_index);
						next_curr_map.put(v1.children.get(0).node_index, v2.children.get(0).node_index);
					}
				}
				final_map.put(i, curr_map.get(i));
			}
			curr_map.clear();
			for (Integer i : next_curr_map.keySet())
				curr_map.put(i, next_curr_map.get(i));
		}
	}
	
	/**
	 * 
	 * @param t1
	 * @param t2
	 * @param match_map
	 */
	private void compareNodesBottomUp(SceneTree t1, SceneTree t2, Map<String, String> match_map){
		
		//base condition
		/** @todo - if one of the tree is completely mapped 
		 *  then the remaining nodes of the other will remain unmapped
		**/
		if(t1.unmapped_list.isEmpty() && t2.unmapped_list.isEmpty()){
			
			StringBuffer sb = new StringBuffer();
			
			for (int i : t1.mapped_map.keySet()) {
				sb.append(i+"->"+t1.mapped_map.get(i) + ", ");
			}
			match_map.put(sb.toString(), computeMatchScore());
		}
		
		
		for (int i=0;i<t1.unmapped_list.size(); i++) {
			
			int v1 = t1.unmapped_list.get(i);
			boolean is_chld_mapped1 = false;
			
			//if no children mapped yet, continue
			for (SceneNode chld1 : t1.node_map.get(v1).children) {
				if(mapped_map.containsKey(chld1.node_index)){
					is_chld_mapped1 = true;
					break;
				}
			}
			
			if(!is_chld_mapped1 && !t1.node_map.get(v1).is_leaf)		continue;	
			
			
			for (int j=0;j<t2.unmapped_list.size(); j++) {
				
				
				int v2 = t2.unmapped_list.get(j);
				boolean is_chld_mapped2 = false;
				
				//if no children mapped yet, continue
				for (SceneNode chld1 : t1.node_map.get(v1).children) {
					if(mapped_map.containsKey(chld1.node_index)){
						is_chld_mapped2 = true;
						break;
					}
				}
				
				if(!is_chld_mapped2 && !t2.node_map.get(v2).is_leaf)		continue;
				
				
				boolean is_chld_match = false;
				
				if(t1.node_map.get(v1).is_leaf && t2.node_map.get(v2).is_leaf)
//						&& Math.abs(t1.node_map.get(v1).node_val-t2.node_map.get(v2).node_val) > 2)
				{
					t1.addToMappedList(v1, v2);
					t2.addToMappedList(v2, v1);
					
					compareNodesBottomUp(t1,t2,match_map);
					
					t1.removeFromMappedList(v1,i);
					t2.removeFromMappedList(v2,j);
				}
				
				//matched only nodes that are in the same depth
				else 
				{
						
					for (SceneNode chld1 : t1.node_map.get(v1).children) {
						for (SceneNode chld2 : t2.node_map.get(v2).children) {
							if(mapped_map.containsKey(chld1.node_index) 
									&& mapped_map.get(chld1.node_index) == chld2.node_index)
							{
								is_chld_match = true;
								break;
							}
						}
						if(is_chld_match)	break;
					}
					
					t1.addToMappedList(v1, is_chld_match?v2:-1);
					t2.addToMappedList(v2, is_chld_match?v2:-1);
					
					compareNodesBottomUp(t1,t2,match_map);
					
					t1.removeFromMappedList(v1,i);
					t2.removeFromMappedList(v2,j);
				}
			}
		}
	}
	
	
	private double calculateNodeDist(SceneNode node1, SceneNode node2)
	{
		double feature_dist = 0;
		
		for(int i = 0; i<node1.features.size(); i++)	
			feature_dist+=(node1.features.get(i)-node2.features.get(i))*(node1.features.get(i)-node2.features.get(i));
		
		return Math.sqrt(feature_dist);
	}
}
