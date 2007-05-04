export LOGFILE=$WEBAPP_JSWORD/buildlog.txt

#export XALAN=$JSWORD_HOME/jar/xalan25d1
#export ANT_OPTS="-Xmx512m"
#export ANT_OPTS="-Xmx512m -Xbootclasspath/p:$XALAN/xalan.jar:$XALAN/xercesImpl.jar:$XALAN/xml-apis.jar"

export PROPERTIES=
export PROPERTIES="$PROPERTIES -Dwebapp.jsword=$WEBAPP_JSWORD"
export PROPERTIES="$PROPERTIES -Dwebapp.bibledesktop=$WEBAPP_BIBLEDESKTOP"
export PROPERTIES="$PROPERTIES -Dftp.base=$FTP_BASE"
export PROPERTIES="$PROPERTIES -Dftp.prefix=$FTP_PREFIX"
export PROPERTIES="$PROPERTIES -Djnlp.homepage=$JNLP_HOMEPAGE"
export PROPERTIES="$PROPERTIES -Djnlp.codebase=$JNLP_CODEBASE"
export PROPERTIES="$PROPERTIES -Djnlp.installed=$JNLP_INSTALLED"
export PROPERTIES="$PROPERTIES -Dkeystore.file=$KEYSTORE_FILE"
