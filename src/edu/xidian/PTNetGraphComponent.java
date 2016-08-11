package edu.xidian;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.shape.mxTokenToShape;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import de.invation.code.toval.graphic.component.DisplayFrame;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;
import de.uni.freiburg.iig.telematik.sepia.petrinet.abstr.AbstractPNNode;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTFlowRelation;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTMarking;
import de.uni.freiburg.iig.telematik.sepia.petrinet.pt.PTNet;

public class PTNetGraphComponent  extends JPanel {
	
	private static final long serialVersionUID = -6795020261692373388L;
	
	protected PTNet petriNet = null;

	private JComponent graphPanel = null;

	protected mxGraph visualGraph = null;
	
	/** vertex of Place style */
	private static final String PlaceStyle = "PlaceStyle"; 
	
	/** vertex of Transition style */
	private static final String TransitionStyle = "TransitionStyle"; 
	
	/** width,height of vertex, default 30 */
	private int vertexWidth = 20, vertexHeight = 20;
		
	/**
	 * key: vertex(Place/Transition) name, value: mxCell对象
	 * for create edge from vertices
	 */
	protected Map<String, mxCell> vertices = new HashMap<String, mxCell>(); 
	
	public PTNetGraphComponent(PTNet petriNet) throws ParameterException  {
		Validate.notNull(petriNet);
		this.petriNet = petriNet;
	}
	
	public void initialize() throws Exception {
		setupVisualGraph();
		setLayout(new BorderLayout(20, 0));
		add(getGraphPanel(), BorderLayout.CENTER);
		setPreferredSize(getGraphPanel().getPreferredSize());
	}
	
	/**
	 * 由petriNet信息，装配visualGraph
	 * insertVextex，isertEdge
	 * @throws Exception
	 */
	protected void setupVisualGraph() throws Exception {	
		visualGraph = new mxGraph();
		Object parent = visualGraph.getDefaultParent();
		createPlaceStyle();
		createTransitionStyle();

		visualGraph.getModel().beginUpdate();
		try{
			for(AbstractPNNode<PTFlowRelation> node: petriNet.getNodes()) {
				String vertexName = node.getName();    // 唯一
				String vertexLabel = node.getLabel();  // 可能不唯一，用于显示
				mxCell vertex = getVertexCell(parent, vertexName, vertexLabel);
				vertices.put(vertexName, vertex);
			}
			for(PTFlowRelation flow: petriNet.getFlowRelations()) {
				String source = flow.getSource().getName();
				String target = flow.getTarget().getName();	
				String id = source + "-" + target;
				// 有向弧的权值，缺省是1，不显示
				Integer constraint = flow.getConstraint();
				String edge = (constraint == 1) ? "" : constraint.toString();
				
				mxCell insertedEdge = (mxCell) visualGraph.insertEdge(parent, id, edge, vertices.get(source),vertices.get(target));
				Object[] cells = new Object[1];
				cells[0] = insertedEdge;
				visualGraph.setCellStyles(mxConstants.STYLE_LABEL_BACKGROUNDCOLOR, "#ffffff", cells);
				visualGraph.setCellStyles(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_TOP,cells);
				visualGraph.setCellStyles(mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_CENTER,cells);
		    }
	
		}
		finally{
			visualGraph.getModel().endUpdate();
		}
		
		
		mxHierarchicalLayout layout = new mxHierarchicalLayout(visualGraph);
		//mxCompactTreeLayout layout = new mxCompactTreeLayout(visualGraph);
		//mxFastOrganicLayout layout = new mxFastOrganicLayout(visualGraph);
		//mxOrganicLayout layout = new mxOrganicLayout(visualGraph);
		
		//layout.setDisableEdgeStyle(false);
		//layout.setDisableEdgeStyle(true); // ok
		//layout.execute(visualGraph.getDefaultParent());
		layout.execute(parent);
	}
	
	/**
	 * 顶点的shape,缺省是"shape=ellipse",
	 * 子类可以重写该方法，定义顶点的形状，如"shape=doubleEllipse";
	 * @param graphVertex
	 * @return
	 */
	protected mxCell getVertexCell(Object parent, String vertexName, String vertexLabel) {
		// Place
		if (petriNet.getPlace(vertexName) != null) {
			return (mxCell) visualGraph.insertVertex(parent, vertexName, toShape(vertexLabel,vertexLabel), 0, 0, 40, 40, PlaceStyle);
		}
		//Transition
		return (mxCell) visualGraph.insertVertex(parent, vertexName, vertexLabel, 0, 0, 40, 40, TransitionStyle);
	}
	
	private JComponent getGraphPanel() {
		if(graphPanel == null){
			graphPanel = new mxGraphComponent(visualGraph);
		}
		return graphPanel;
	}
	
	public void setNodeColor(String color, String... nodeNames){
		Object[] cells = new Object[nodeNames.length];
		for(int i=0; i<nodeNames.length; i++)
			cells[i] = vertices.get(nodeNames[i]);
		visualGraph.setCellStyles(mxConstants.STYLE_FILLCOLOR, color, cells);
	}
	
	private int getPlaceTokenNumber(String placeName) {
		PTMarking initialMarking = petriNet.getInitialMarking();
		Integer tokenNum = initialMarking.get(placeName);
		if (tokenNum == null) tokenNum = 0;
		return tokenNum; 
	}
	
	/** 向顶点形状传递 place label(库所显示标识)和token number */
	private mxTokenToShape toShape(String placeName,String placeLabel) {
		int tokenNumber = getPlaceTokenNumber(placeName);	
		return new mxTokenToShape(placeLabel,tokenNumber);
	}
	
	/**
	 * vertex of place style
	 * @return styleName
	 */
	private void createPlaceStyle() {
		// defaultVertex={shape=rectangle, fontColor=#774400, strokeColor=#6482B9, fillColor=#C3D9FF, align=center, verticalAlign=middle}
		// defaultEdge={endArrow=classic, shape=connector, fontColor=#446299, strokeColor=#6482B9, align=center, verticalAlign=middle}, 
		mxStylesheet stylesheet = visualGraph.getStylesheet();
		Hashtable<String, Object> style = new Hashtable<String, Object>();
		style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_TOKEN_ELLIPSE);
		style.put(mxConstants.STYLE_FILLCOLOR, "#C3D9FF");
		style.put(mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_LEFT); //the horizontal label position of vertices
		style.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_TOP); //the vertical label position of vertices
		stylesheet.putCellStyle(PlaceStyle, style);
	}
	
	/**
	 * vertex of Transition style
	 * @return styleName
	 */
	private void createTransitionStyle() {
		mxStylesheet stylesheet = visualGraph.getStylesheet();
		Hashtable<String, Object> style = new Hashtable<String, Object>();
		style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
		style.put(mxConstants.STYLE_FILLCOLOR, "#C3D9FF");
		style.put(mxConstants.STYLE_LABEL_POSITION, mxConstants.ALIGN_LEFT); //the horizontal label position of vertices
		style.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_TOP); //the vertical label position of vertices
		stylesheet.putCellStyle(TransitionStyle, style);
	}
	
	
	/**
	 * get width of vertex,{@link #width}
	 */
	public int getVertexWidth() {
		return this.vertexWidth;
	}

	/**
	 * set width of vertex,{@link #width}
	 * @param width
	 */
	public void setVertexWidth(int width) {
		this.vertexWidth = width;
	}

	/**
	 * get height of vertex,{@link #width}
	 */
	public int getVertexHeight() {
		return this.vertexHeight;
	}

	/**
	 * set height of vertex,{@link #width}
	 * @param height
	 */
	public void setVertexHeight(int height) {
		this.vertexHeight = height;
	}

	public static void main(String[] args) {
		PTNet ptnet = CreatePetriNet.createPTnet1();
		PTNetGraphComponent component = new PTNetGraphComponent(ptnet);
		try {
			component.initialize();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    new DisplayFrame(component,true);
	}

}