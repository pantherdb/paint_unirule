function getParameter(queryString, parameter) {
  var paramRegExp = new RegExp("[\\?&]" + parameter + "=(\\w*)");
  var result = queryString.match(paramRegExp);
  return result == null ? null : result[1];
}
function refreshPortlet(portletReference, pageURL) {
  if (document.getElementById) {
    var rimg = document.images["refresh_" + portletReference];
    if (rimg) rimg.src = "/portalimages/prefresha.gif";
    var debugLevel = getParameter(location.search, "_debug");
    document.getElementById("portalIFrame").src = 
       pageURL + (pageURL.indexOf("?") == -1 ? "?" : "&") + "_portletRefresh=" + escape(portletReference)
       + (debugLevel != null ? "&_debug=" + debugLevel : "");
  }
  else
    location.href = pageURL;
}
function refreshPortletIFrame(portletReference, pageURL) {
  document.getElementById("portalIFrame_" + portletReference).src = 
     pageURL + (pageURL.indexOf("?") == -1 ? "?" : "&") + "_portletRefresh=" + escape(portletReference);
}
var _tmpImg = new Image();
function high(imgname) {
  var img = eval("document.images.i" + imgname);
  if (img) {
    _tmpImg.src = img.src;
    img.src = eval("high" + imgname + ".src");
  }
}
function low(imgname) {;
  var img = eval("document.images.i" + imgname);
  if (img) {
    img.src = _tmpImg.src;
  }
}
function folderpropertysheet(cornerid,siteid) {window.open("http://www.oracle.com/wocportal/pls/wocprod/WOCPROD.wwpob_property_ui.page_property_sheet?p_cornerid="+cornerid+"&p_siteid="+siteid,"propertysheet","scrollbars=1,resizable=1,width=500,height=500").focus();}
function propertysheet(thingid,masterthingid,cornerid,siteid) {window.open("http://www.oracle.com/wocportal/pls/wocprod/WOCPROD.wwpob_property_ui.item_property_sheet?p_thingid="+thingid+"&p_masterthingid="+masterthingid+"&p_cornerid="+cornerid+"&p_siteid="+siteid,"propertysheet","scrollbars=1,resizable=1,width=500,height=500").focus();}
function copyBody(portletReference) {
  if (document.styleSheets) {
    var rules, ruleslen;
    for (var s=0; s < document.styleSheets.length; s++) {
      if (document.styleSheets[s].cssRules)
        rules = document.styleSheets[s].cssRules;
      else
        rules = document.styleSheets[s].rules;
      ruleslen = (rules ? rules.length : 0);
      for (var r=0; r<ruleslen; r++) {
        if (parent.document.styleSheets[0].insertRule) {
          try {
            parent.document.styleSheets[0].insertRule(
              rules[r].selectorText + "{" + rules[r].style.cssText + "}",
              parent.document.styleSheets[0].cssRules.length);
          }
          catch (e) { // this is likely to happen on Apple Safari
            null;
            /* alert("exception - insertRule("
               + rules[r].selectorText + "{" + rules[r].style.cssText + "},"
               + parent.document.styleSheets[0].cssRules.length + ")"); */
          }
        }
        else {
          parent.document.styleSheets[0].addRule(
            rules[r].selectorText,
            rules[r].style.cssText ? rules[r].style.cssText : " ");
        }
      }
    }
  }
  var srcScripts = document.getElementsByTagName("script");
  var destHead   = parent.document.getElementsByTagName("head").item(0);
  for (var i=0; i<srcScripts.length; i++)
  {
      var newScript = parent.document.createElement("script");
      newScript.type = srcScripts.item(i).type;
      if (srcScripts.item(i).src)
        newScript.src  = srcScripts.item(i).src;
      newScript.text = srcScripts.item(i).text;
      destHead.appendChild(newScript);
  }
  parent.document.getElementById("p" + portletReference).innerHTML = document.body.innerHTML;
}


function removePortlet(pRef)
{
  if (confirm("Are you sure you want to remove the portlet from this page? You can reinstate this portlet by clicking on the Personalize Link on the Page."))
  {
    document.getElementById("p" + pRef).style.display = "none";
    location.href = "http://www.oracle.com/wocportal/pls/wocprod/WOCPROD.wwpob_page_dialogs.remove_portlet_dlg" + "?p_portlet_ref=" + pRef;
  }
}

var restoreImg = null;
function collapsePortlet(pRef, pageUrl)
{
  if (document.getElementById)
  {
    // get the portlet contents
    var e = document.getElementById("pcnt" + pRef);

    // determine the intended state by looking at the collapse/expand state
    var st = (e.style.display == "none" ? 0 : 1);

    // if collapsing, show the restore image, and dynamically hide content
    if (st == 1)
    {
      // the restore image is cached
      if (!restoreImg)
      {
        restoreImg = new Image();
        restoreImg.src = "/portalimages/restore.gif";
        restoreImg.alt = "Restore";
      }

      // hide the portlet content and show restore image
      e.style.display = "none";
      var cimg = document.images["collapse_" + pRef];
      if (cimg) { cimg.src = restoreImg.src; cimg.alt = restoreImg.alt; }
    }

    // This URL returns nothing, but updates the collapse/restore state
    var u = "http://www.oracle.com/wocportal/pls/wocprod/WOCPROD.wwpob_page_dialogs.collapse_portlet?p_portlet_ref=" + pRef + "&p_state=" + st;

    // Call the collapse/restore URL.  If restoring,
    // then do this synchronously (before refreshing).
    if (window.XMLHttpRequest) {    // W3C
      var req = new XMLHttpRequest();
      req.open("GET", u, st != 0);
      req.send(null);
    }
    else if (window.ActiveXObject) {    // IE
      var req = new ActiveXObject("Microsoft.XMLHTTP");
      if (req) {
        req.open("GET", u, st != 0);
        req.send();
      }
    }

    // Refresh contents if restoring from collapsed state
    if (st == 0)
      refreshPortlet(pRef, pageUrl);
  }
}
    
function show_context_help(h) {window.open(h,"Help","menubar=1,toolbar=1,scrollbars=1,resizable=1,width=700,height=500").focus();}
