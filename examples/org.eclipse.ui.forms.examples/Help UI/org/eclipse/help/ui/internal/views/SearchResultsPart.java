/*
 * Created on Dec 13, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.help.ui.internal.views;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.help.internal.search.SearchHit;
import org.eclipse.help.ui.internal.search.*;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.forms.*;
import org.eclipse.ui.forms.events.*;
import org.eclipse.ui.forms.examples.internal.ExamplesPlugin;
import org.eclipse.ui.forms.widgets.*;
import org.eclipse.ui.help.WorkbenchHelp;

/**
 * @author dejan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchResultsPart extends AbstractFormPart implements IHelpPart {
	private ReusableHelpPart parent;
	//private ScrolledFormText stext;
	private FormText searchResults;
	private SorterByScore resultSorter;
	private String id;
	private String phrase;
	/**
	 * @param parent
	 * @param toolkit
	 * @param style
	 */
	public SearchResultsPart(Composite parent, FormToolkit toolkit) {
		//stext = new ScrolledFormText(parent, false);
		//toolkit.adapt(stext);
		resultSorter = new SorterByScore();
		searchResults = toolkit.createFormText(parent, true);
		searchResults.marginWidth = 5;
		//stext.setFormText(searchResults);
		searchResults.setColor(FormColors.TITLE, toolkit.getColors().getColor(
				FormColors.TITLE));
		//searchResults.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_GREEN));
		searchResults.setImage(ExamplesPlugin.IMG_HELP_TOPIC, ExamplesPlugin
				.getDefault().getImage(ExamplesPlugin.IMG_HELP_TOPIC));
		searchResults.setImage(ExamplesPlugin.IMG_NW, ExamplesPlugin
				.getDefault().getImage(ExamplesPlugin.IMG_NW));
		searchResults.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				Object href = e.getHref();
				if (href.toString().equals("_more"))
					doMoreResults(phrase);
				else
					doOpenLink(e.getHref());
			}
		});
		searchResults.setText("", false, false);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.help.ui.internal.views.IHelpPart#getControl()
	 */
	public Control getControl() {
		return searchResults;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.help.ui.internal.views.IHelpPart#init(org.eclipse.help.ui.internal.views.NewReusableHelpPart)
	 */
	public void init(ReusableHelpPart parent, String id) {
		this.parent = parent;
		this.id = id;
	}
	public String getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.help.ui.internal.views.IHelpPart#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		searchResults.setVisible(visible);
	}
	void clearResults() {
		searchResults.setText("", false, false);
		parent.reflow();
	}
	void updateResults(String phrase, StringBuffer buff, HelpSearchResult hresult) {
		this.phrase = phrase;
		buff.delete(0, buff.length());
		if (hresult.getMatchCount() > 0) {
			buff.append("<form>");
			buff.append("<p><span color=\"");
			buff.append(FormColors.TITLE);
			buff.append("\">Search results:</span></p>");
			Object[] elements = hresult.getElements();
			resultSorter.sort(null, elements);

			for (int i = 0; i < elements.length; i++) {
				SearchHit hit = (SearchHit) elements[i];
				buff.append("<li indent=\"21\" style=\"image\" value=\"");
				buff.append(ExamplesPlugin.IMG_HELP_TOPIC);
				buff.append("\">");
				buff.append("<a href=\"");
				buff.append(hit.getHref());
				buff.append("\">");
				buff.append(hit.getLabel());
				buff.append("</a>");
				buff.append(" <a href=\"");
				buff.append("nw:");
				buff.append(hit.getHref());
				buff.append("\"><img href=\"");
				buff.append(ExamplesPlugin.IMG_NW);
				buff.append("\" alt=\"Open link in a Help window\"");
				buff.append("/>");
				buff.append("</a>");
				buff.append("</li>");
			}
			if (elements.length > 0) {
				buff.append("<p><a href=\"_more\">More results...</a></p>");
			}
			buff.append("</form>");
			searchResults.setText(buff.toString(), true, false);
		} else
			searchResults.setText("", false, false);
		parent.reflow();
	}	
	
	private void doMoreResults(String phrase) {
		if (parent.isInWorkbenchWindow())
			doWorkbenchSearch(phrase);
		else
			doExternalSearch(phrase);
	}
	
	private void doWorkbenchSearch(String phrase) {
		SearchPart part = (SearchPart)parent.findPart(IHelpViewConstants.SEARCH);
		if (part!=null)
			part.startWorkbenchSearch(phrase);
	}

	private void doExternalSearch(String phrase) {
		try {
			String ephrase = URLEncoder.encode(phrase, "UTF-8"); //$NON-NLS-1$
			String query = "tab=search&searchWord=" + ephrase; //$NON-NLS-1$
			WorkbenchHelp.displayHelpResource(query);
		} catch (UnsupportedEncodingException e) {
			System.out.println(e);
		}
	}

	private void doOpenLink(Object href) {
		String url = (String)href;

		if (url.startsWith("nw:")) {
			WorkbenchHelp.displayHelpResource(url.substring(3));
		}
		else 
			parent.showURL(url);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.help.ui.internal.views.IHelpPart#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public boolean fillContextMenu(IMenuManager manager) {
		return parent.fillFormContextMenu(searchResults, manager);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.help.ui.internal.views.IHelpPart#hasFocusControl(org.eclipse.swt.widgets.Control)
	 */
	public boolean hasFocusControl(Control control) {
		return searchResults.equals(control);
	}
}