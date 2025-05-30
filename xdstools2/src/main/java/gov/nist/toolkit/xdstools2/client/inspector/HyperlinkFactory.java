package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.registrymetadata.client.Association;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.registrymetadata.client.MetadataObject;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.ObjectRefs;
import gov.nist.toolkit.registrymetadata.client.ResourceItem;
import gov.nist.toolkit.registrymetadata.client.Uid;
import gov.nist.toolkit.results.client.AssertionResults;
import gov.nist.toolkit.results.client.StepResult;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;
import gov.nist.toolkit.xdstools2.client.TestDocumentation;
import gov.nist.toolkit.xdstools2.client.tabs.GetRelatedTab;
import gov.nist.toolkit.xdstools2.client.widgets.AccessControlledMenuItem;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;

public class HyperlinkFactory {

	static Hyperlink metadataUpdate(MetadataInspectorTab it, DocumentEntry de, QueryOrigin queryOrigin, String text) {
		Hyperlink h = new Hyperlink();
		h.setText(text);
		h.addClickHandler(new MuClickHandler(it, de, queryOrigin));
		return h;
	}
	
	static Hyperlink link(MetadataInspectorTab it, AssertionResults ar, String text) {
		Hyperlink h = new Hyperlink();
		h.setHTML(text);
		h.addClickHandler(new AssertionResultSelector(it, ar));
		return h;
	}
	
	static Hyperlink getDocuments(MetadataInspectorTab it, StepResult originatingResult, ObjectRefs ids, String text, boolean isLid, SiteSpec siteSpec)  {
		Hyperlink h = new Hyperlink();
		h.setText(text);
		h.addClickHandler(new GetDocuments(it, originatingResult, ids, isLid, siteSpec));
		return h;
	}
	
	static String prefix(String in, int maxlen) {
		if (in.length() <= maxlen)
			return in;
		return in.substring(0, maxlen-1);
	}
	
	static Hyperlink linkXMLView(MetadataInspectorTab it, String linkText, String xml) {
		if (xml == null)
			xml = "";
		if (linkText == null)
			linkText = "";
		Hyperlink h = new Hyperlink();
		h.setText(linkText);
		h.addClickHandler(new XMLViewSelector(it, xml));
		return h;
	}

	static Hyperlink linkMainXMLView(MetadataInspectorTab it, String linkText, String xml) {
		if (xml == null)
			xml = "";
		if (linkText == null)
			linkText = "";
		Hyperlink h = new Hyperlink();
		h.setText(linkText);
		h.addClickHandler(new XMLMainViewSelector(it, xml));
		return h;
	}

	static Widget link(MetadataInspectorTab it, Association mo, MetadataCollection mc) {
		FlowPanel panel = new FlowPanel();

		Hyperlink h;

		h = new Hyperlink();
		h.setText(prefix(mo.displayName(), 40));
		h.addClickHandler(new HistorySelector(it, mo));
		panel.add(h);

		String sourceId = mo.source;
		MetadataObject sourceObj = mc.findObject(sourceId);
		if (sourceObj == null) {
			HorizontalFlowPanel hfp = new HorizontalFlowPanel();
			hfp.add(new HTML("Source: "));
			h = new Hyperlink();
			h.setText(sourceId);
			h.addClickHandler(new GetObjects(it, new ObjectRef(sourceId)));
			hfp.add(h);
			panel.add(hfp);
		} else {
			HorizontalFlowPanel hfp = new HorizontalFlowPanel();
			hfp.add(new HTML("Source: "));
			h = new Hyperlink();
			h.setText(sourceObj.displayName());
			h.addClickHandler(new HistorySelector(it, sourceObj));
			hfp.add(h);
			panel.add(hfp);
		}

		String targetId = mo.target;
		MetadataObject targetObj = mc.findObject(targetId);
		if (targetObj == null) {
			HorizontalFlowPanel hfp = new HorizontalFlowPanel();
			hfp.add(new HTML("Target: "));
			h = new Hyperlink();
			h.setText(targetId);
			h.addClickHandler(new GetObjects(it, new ObjectRef(targetId)));
			hfp.add(h);
			panel.add(hfp);
		} else {
			HorizontalFlowPanel hfp = new HorizontalFlowPanel();
			hfp.add(new HTML("Target: "));
			h = new Hyperlink();
			h.setText(targetObj.displayName());
			h.addClickHandler(new HistorySelector(it, targetObj));
			hfp.add(h);
			panel.add(hfp);
		}

		return panel;
	}

	static Widget link(MetadataInspectorTab it, ObjectRef o) {
		Hyperlink h = new Hyperlink();
		h.setText(o.id);
		h.addClickHandler(new HistorySelector(it, o));
		return h;
	}

	static Widget link(MetadataInspectorTab it, MetadataObject mo) {
		Hyperlink h = new Hyperlink();
		h.setText(prefix(mo.displayName(), 40));
		if (mo.displayName()!=null && mo.displayName().length() > 40) {
			h.setTitle(mo.displayName());
		}
		h.addClickHandler(new HistorySelector(it, mo));
		return h;
	}

	static Widget link(MetadataInspectorTab it, ResourceItem ri) {
		Hyperlink h = new Hyperlink();
		h.setText(prefix(ri.displayName(), 40));
		h.addClickHandler(new HistorySelector(it, ri));
		return h;
	}

	static HTML html(MetadataInspectorTab it, MetadataObject mo) {
		HTML h = new HTML();
		h.setText(prefix(mo.displayName(), 40));
		h.addClickHandler(new HistorySelector(it, mo));
		return h;
	}

	static Widget link(MetadataInspectorTab it, MetadataObject mo, String text) {
		Hyperlink h = new Hyperlink();
		h.setText(text);
		h.addClickHandler(new HistorySelector(it, mo));
		return h;
	}

	static Widget link(MetadataInspectorTab it, MetadataCollection metadataCollection, ObjectRef or) {
		MetadataObject mo = metadataCollection.findObject(or.id);
		if (mo == null) {
			Hyperlink h = new Hyperlink();
			h.setText(or.id + " (load now)");
			h.addClickHandler(new GetObjects(it, or));  // needs to come from ObjectRef parm
			return h;
		}
		return link(it, mo);
	}

	static Widget link(MetadataInspectorTab it, MetadataCollection metadataCollection, String id, String text) {
		MetadataObject mo = metadataCollection.findObject(id);
		if (mo == null) {
			Hyperlink h = new Hyperlink();
			h.setText("External: " + text);
			return h;
		}
		return link(it, mo, text);
	}

	public static HTML addHTML(String html) {
		HTML msgBox = new HTML();
		msgBox.setHTML(html);
		return msgBox;		
	}

	public static Hyperlink getRelated(MetadataInspectorTab it, ObjectRef or, String text) {
		Hyperlink h = new Hyperlink();
		h.setText(text);
		h.addClickHandler(new GetRelated(it, or, GetRelatedTab.getAllAssocTypes()));
		return h;
	}
	
	public static Hyperlink retrieve(MetadataInspectorTab it, DocumentEntry de, String text) {
		Hyperlink h = new Hyperlink();
		h.setText(text);
		h.addClickHandler(new Retrieve(it, new Uid(de)));
		return h;
	}
	
//	public static Hyperlink edit(MetadataInspectorTab it, RegistryObject de, TabContainer container, SiteSpec siteSpec) {
//		return HyperlinkFactory.launchTool(ToolLauncher.metadataEditorTabLabel, new ToolLauncher(container, ToolLauncher.metadataEditorTabLabel, siteSpec, de));
//	}
	
	public static Hyperlink getSubmissionSets(MetadataInspectorTab it, ObjectRef or, String text) {
		Hyperlink h = new Hyperlink();
		h.setText(text);
		h.addClickHandler(new GetSubmissionSets(it, or));
		return h;
	}
	
	public static Hyperlink getAssociations(MetadataInspectorTab it, ObjectRef or, String text) {
		Hyperlink h = new Hyperlink();
		h.setText(text);
		h.addClickHandler(new GetAssociations(it, or));
		return h;
	}
	
	public static Hyperlink getFoldersForDocument(MetadataInspectorTab it, ObjectRef or, String text) {
		Hyperlink h = new Hyperlink();
		h.setText(text);
		h.addClickHandler(new GetFoldersForDocument(it, or));
		return h;
	}
	
	public static Hyperlink getFolderAndContents(MetadataInspectorTab it, ObjectRef or, String text) {
		Hyperlink h = new Hyperlink();
		h.setText(text);
		h.addClickHandler(new GetFolderAndContents(it, or));
		return h;
	}
	
	public static Hyperlink link(String html, ClickHandler clickHandler) {
		Hyperlink h = new Hyperlink();
		h.setHTML(html);
		h.addClickHandler(clickHandler);
		return h;
	}
	
	public static Hyperlink launchTool(String html, ClickHandler clickHandler) {
		Hyperlink h = new Hyperlink();
		h.setHTML(html);
		if (clickHandler != null)
			h.addClickHandler(clickHandler);
		h.setTitle("Launch this tool in a separate tab");
		return h;
	}

	public static AccessControlledMenuItem launchAdminTool(String html, final ClickHandler clickHandler) {
		Anchor a = new Anchor(false);
		a.setText(html);
		a.setTitle("Launch this tool in a separate tab");
		a.getElement().getStyle().setCursor(Style.Cursor.POINTER);
		return new AccessControlledMenuItem<Anchor>(a, clickHandler, AccessControlledMenuItem.IndicatorType.LOCK_ICON) {
			@Override
			public boolean isAccessible() {
				return  PasswordManagement.isSignedIn;
			}
		};
	}


	public static Hyperlink documentation(String html, String testname) {
		Hyperlink h = new Hyperlink();
		h.setHTML(html);
		h.addClickHandler(new HyperlinkFactory().new DocumentationClickHandler(testname));
		return h;
	}
	
	class DocumentationClickHandler implements ClickHandler {
		String testname;
		
		DocumentationClickHandler(String testname) {
			this.testname = testname;
		}
		
		public void onClick(ClickEvent event) {
			String docs = TestDocumentation.getDocumentation(testname);
			if (docs == null)
				docs = "No documentation available";
			new PopupMessage(docs);
		}
	}

}
