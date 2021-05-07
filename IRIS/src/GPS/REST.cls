Class GPS.REST Extends %CSP.REST
{

Parameter HandleCorsRequest = 1;

XData UrlMap [ XMLNamespace = "http://www.intersystems.com/urlmap" ]
{
<Routes>
<Route Url="/latestinfo" Method="GET" Call="GetLatestInfo" Cors="true"/>
</Routes>
}

ClassMethod GetLatestInfo() As %Status
{
    #dim ex As %Exception.AbstractException
    #dim %response As %CSP.Response
    #dim %request As %CSP.Request
    set st=$$$OK
    set %response.CharSet="utf8"
    set %response.ContentType="application/json"
    try {
        set sql="select JSON_OBJECT('lat':lat,'lng':lon, 'heading':heading) as json FROM JavaXEPSample.GPXInput order by ID"
        set stmt=##class(%SQL.Statement).%New()
        $$$ThrowOnError(stmt.%Prepare(sql))
        #dim rset As %SQL.StatementResult
        set rset=stmt.%Execute()
        set array=[]
        while rset.%Next() {
            do array.%Push({}.%FromJSON(rset.%Get("json")))
        }
        do array.%ToJSON()
    }
    catch ex {
        set st=ex.AsStatus()
    }
    return st
}

}
