' Test real XML parsing

xmlString = "<root><person><name>Alice</name><age>30</age></person></root>"
doc = Xml.Parse(xmlString)
Console.WriteLine("XML document parsed: " + doc)

name = Xml.GetText(doc, "//name")
age = Xml.GetText(doc, "//age")

Console.WriteLine("Name: " + name)
Console.WriteLine("Age: " + age)

Console.WriteLine("XML test complete!")
