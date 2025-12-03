' Test real XML parsing

Dim xmlString As String
Dim doc As Integer
Dim name As String
Dim age As String

xmlString = "<root><person><name>Alice</name><age>30</age></person></root>"
doc = Xml.Parse(xmlString)
Console.WriteLine("XML document parsed: " + doc)

name = Xml.GetText(doc, "//name")
age = Xml.GetText(doc, "//age")

Console.WriteLine("Name: " + name)
Console.WriteLine("Age: " + age)

Console.WriteLine("XML test complete!")
