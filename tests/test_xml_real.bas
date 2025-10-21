Rem Test real XML parsing

Dim xmlString As String = "<root><person><name>Alice</name><age>30</age></person></root>"
Dim doc As Integer = Xml.Parse(xmlString)
Print "XML document parsed: "; doc

Dim name As String = Xml.GetText(doc, "//name")
Dim age As String = Xml.GetText(doc, "//age")

Print "Name: "; name
Print "Age: "; age

Print "XML test complete!"

