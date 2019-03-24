package lovdream.android.cit;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandler extends DefaultHandler {
	private static final String XML_TAG = "XMLHandler";
	private List<Integer> arrayLineKeyCount;
	private List<String> arrayTitle;
	private List<Integer> arrayvkValue;
	private int g_nLineKeyCount[];
	private String g_strTitle[];
	private int g_vkValue[];
	private boolean in_key_tag;
	private boolean in_keycount_tag;
	private boolean in_keyname_tag;
	private boolean in_keyvalue_tag;
	private boolean in_line_tag;
	private boolean in_linecount_tag;
	private boolean in_ncolumeCount;
	private int nLineKeyCount;
	private int ncolumeCount;
	private int nkeyCount;
	private int nlineCount;

	public XMLHandler() {
		in_keycount_tag = false;
		in_linecount_tag = false;
		in_ncolumeCount = false;
		in_line_tag = false;
		in_key_tag = false;
		in_keyname_tag = false;
		in_keyvalue_tag = false;
		nkeyCount = 0;
		nlineCount = 0;
		ncolumeCount = 0;
		arrayLineKeyCount = new ArrayList<Integer>();
		arrayTitle = new ArrayList<String>();
		arrayvkValue = new ArrayList<Integer>();
	}

	public void characters(char ac[], int i, int j) {
		String s = new String(ac, i, j);
		if (in_keycount_tag) {
			nkeyCount = Integer.parseInt(s);
			Log.e("XMLHandler", nkeyCount + "");
		}
		if (in_linecount_tag) {
			nlineCount = Integer.parseInt(s);
			Log.e("XMLHandler", "nlineCount = " + nlineCount);
		}
		if (in_ncolumeCount) {
			ncolumeCount = Integer.parseInt(s);
			Log.e("XMLHandler", "ncolumeCount = " + ncolumeCount);
		}
		if (in_keyname_tag)
			arrayTitle.add(s);
		if (in_keyvalue_tag) {
			arrayvkValue.add(Integer.parseInt(s));
		}
	}

	public void endDocument() throws SAXException {
	}

	public void endElement(String s, String s1, String s2) throws SAXException {
		if (s1.equals("keycount"))
			in_keycount_tag = false;
		if (s1.equals("linecount"))
			in_linecount_tag = false;
		if (s1.equals("colume"))
			in_ncolumeCount = false;
		if (s1.equals("line"))
			arrayLineKeyCount.add(nLineKeyCount);
		if (s1.equals("keyname"))
			in_keyname_tag = false;
		if (s1.equals("keyvalue"))
			in_keyvalue_tag = false;
		if (s1.equals("line")) {
			g_strTitle = new String[nkeyCount];
			g_nLineKeyCount = new int[nlineCount];
			g_vkValue = new int[nkeyCount];
			int i = 0;
			do {
				int j = arrayTitle.size();
				if (i >= j)
					break;
				String as1[] = g_strTitle;
				String s3 = (String) arrayTitle.get(i);
				as1[i] = s3;
				StringBuilder stringbuilder = (new StringBuilder())
						.append("g_strTitle[i] = ");
				String s4 = g_strTitle[i];
				String s5 = stringbuilder.append(s4).toString();
				Log.e("XMLHandler", s5);
				i++;
			} while (true);
			i = 0;
			do {
				if (i >= arrayLineKeyCount.size())
					break;
				g_nLineKeyCount[i] = arrayLineKeyCount.get(i);
				Log.e("XMLHandler", "g_nLineKeyCount[i] = "
						+ g_nLineKeyCount[i]);
				i++;
			} while (true);
			i = 0;
			do {
				if (i >= arrayvkValue.size())
					break;
				g_vkValue[i] = ((Integer) arrayvkValue.get(i)).intValue();
				Log.e("XMLHandler", "arrayvkValue[i] = " + g_vkValue[i]);
				i++;
			} while (true);
		}
	}

	public int[] getArrayLineKeyCount() {
		return g_nLineKeyCount;
	}

	public String[] getArrayTitle() {
		return g_strTitle;
	}

	public int[] getArrayvkValue() {
		return g_vkValue;
	}

	public int getColumeCount() {
		return ncolumeCount;
	}

	public int getKeyCount() {
		return nkeyCount;
	}

	public int getLineCount() {
		return nlineCount;
	}

	public void startDocument() throws SAXException {
	}

	public void startElement(String s, String s1, String s2,
			Attributes attributes) throws SAXException {
		if (s1.equals("keycount")) // goto _L2; else goto _L1
			in_keycount_tag = true;
		else if (s1.equals("linecount"))
			in_linecount_tag = true;
		else if (s1.equals("colume"))
			in_ncolumeCount = true;
		else if (s1.equals("line"))
			nLineKeyCount = 0;
		else if (s1.equals("key"))
			nLineKeyCount++;
		else if (s1.equals("keyname"))
			in_keyname_tag = true;
		else if (s1.equals("keyvalue"))
			in_keyvalue_tag = true;
	}

}
