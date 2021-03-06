/**
This package contains the BookDataDisplay interface which is designed to
allow GUI components to choose between the methods they use to display BookData
objects.

<br/>
There are several implementation of this interface in sub-packages.
<ul>
<li>textpane - A simple unscrolled implementation that uses a JDK HTML widget.</li>
<li>proxy - A handy base class to simplify adding features to another
    implementation of BookDataDisplay</li>
<li>scrolled - Builds on proxy to scroll child BookDataDisplays. Generally the
    child will be a textpane implementation.</li>
<li>tab - Splits a display into a number of tabs so arbitarily large pasasges
    can be viewed without scrolling or memory problems.</li>
<li>splitlist - presents the parts of a passage as a list from which individual
    keys can be selected for viewing.</li>
</ul>


<br/>
We have spent some time investigating alternatives to the JDK HTML widget.
The requirements were:
<ul>
<li>work using OSIS input</li>
<li>be easily understood for the developer (which fairly much means HTML)</li>
<li>work over webstart</li>
<li>be freely re-distributable</li>
<li>allow some DHTML like flexibility</li>
</ul>

<p>
<b>JRex (Embed Mozilla)</b><br/>
Very good html displayer, but probably very complex to get working and
installed, especially over WebStart. JNI+webstart anyone?
See <a href="http://jrex.mozdev.org/index.html">mozdev</a>.
</p>

<p>
<b>Jazilla (Mozilla re-write in Java)</b><br/>
Interesting project, but one that seems more interested in XUL than the Java
renderer, proceeding slowly.
See <a href="http://jazilla.mcbridematt.sniperhq.net/">their home page</a>.
</p>

<p>
<b>JXWB (OSS Java Browser)</b><br/>
Commercial software made free. Works well with Swing (includes customized
implementations of javax.swing.text.Document and javax.swing.text.EditorKit)
Things have moved on quite a bit recently.
See <a href="http://sourceforge.net/projects/jxwb">JXWB on SourceForge</a>
</p>

<p>
<b>SWT Browser Component</b><br/>
Don't think we could easily embed an SWT component in swing (since SWT is
heavyweight in AWT speak) and I don't particularly want to re-write the whole
app in SWT. However there are 2 projects to allow use of SWT from a swing API.
<a ref="http://swingwt.sourceforge.net/">SwingWT</a> is a swing-like proxy to
SWT and <a href="http://chrriis.brainlex.com/projects/mastercl/">MasterCL</a>
will allow you to dynamically swap package names to make the whole of swing use
SWT dynamically.
It is possible to use SWT from webstart -
<a href="http://irate.sf.net/">irate radio</a> does it although their build
scripts are not very helpful.
</p>

<p>
<b>FOP -&gt; PNG</b><br/>
FOP is XSL:FO renderer that can create PDFs and various graphics files.
Could give very slick output, but not at all dynamic, quite slow and FO is not
easy to use. Probably not one of the best ideas for normal rendering.
See <a href="http://xml.apache.org/fop/index.html">FOP at Apache</a>.
</p>

<p>
<b>Dynamic Swing GUI</b><br/>
There are plenty of XML-&gt;Swing converters. JDK 1.4 even includes one.
Maybe we could write a OSIS-&gt;SwingXML converter in XSL and then render OSIS
text in swing components. Could be very fancy. XSL could be complex.
</p>
 * 
 */
package org.crosswire.bibledesktop.display;
