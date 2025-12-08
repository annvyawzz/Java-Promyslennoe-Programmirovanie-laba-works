package com.psyche.xml;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XMLHelper {
    private static final String XML_PATH = "library.xml";

    // Методы для старой версии (совместимость)
    public static List<Book> loadBooks() {
        try {
            return getAllBooks();
        } catch (Exception e) {
            System.err.println("Ошибка загрузки: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void saveBooks(List<Book> books) {
        try {
            // Простая реализация - сохраняем весь список
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("library");
            doc.appendChild(root);

            for (Book book : books) {
                Element bookElement = doc.createElement("book");
                bookElement.setAttribute("id", String.valueOf(book.getId()));
                bookElement.setAttribute("copies", String.valueOf(book.getCopies()));
                bookElement.setAttribute("available", String.valueOf(book.getAvailable()));

                bookElement.appendChild(createElement(doc, "title", book.getTitle()));
                bookElement.appendChild(createElement(doc, "author", book.getAuthor()));
                bookElement.appendChild(createElement(doc, "year", String.valueOf(book.getYear())));
                bookElement.appendChild(createElement(doc, "price", String.valueOf(book.getPrice())));
                bookElement.appendChild(createElement(doc, "category", book.getCategory()));

                root.appendChild(bookElement);
            }

            saveDocument(doc);
        } catch (Exception e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
        }
    }

    // ========== XPATH МЕТОДЫ ==========

    public static List<Book> getAllBooks() throws Exception {
        Document doc = loadDocument();
        XPath xpath = XPathFactory.newInstance().newXPath();

        String expression = "/library/book";
        NodeList nodes = (NodeList) xpath.compile(expression).evaluate(doc, XPathConstants.NODESET);

        List<Book> books = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            books.add(nodeToBook((Element) nodes.item(i)));
        }

        return books;
    }

    public static Book findBookById(int id) throws Exception {
        Document doc = loadDocument();
        XPath xpath = XPathFactory.newInstance().newXPath();

        String expression = String.format("/library/book[@id='%d']", id);
        Node bookNode = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

        if (bookNode != null) {
            return nodeToBook((Element) bookNode);
        }
        return null;
    }

    public static void updateBookPrice(int id, double newPrice) throws Exception {
        Document doc = loadDocument();
        XPath xpath = XPathFactory.newInstance().newXPath();

        String expression = String.format("/library/book[@id='%d']/price", id);
        Node priceNode = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

        if (priceNode != null) {
            priceNode.setTextContent(String.valueOf(newPrice));
            saveDocument(doc);
        } else {
            throw new Exception("Книга с ID=" + id + " не найдена");
        }
    }

    public static void updateAvailableCount(int id, int newAvailable) throws Exception {
        Document doc = loadDocument();
        XPath xpath = XPathFactory.newInstance().newXPath();

        String expression = String.format("/library/book[@id='%d']", id);
        Node bookNode = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

        if (bookNode != null) {
            Element bookElement = (Element) bookNode;
            bookElement.setAttribute("available", String.valueOf(newAvailable));
            saveDocument(doc);
        }
    }

    public static List<Book> findBooksByAuthor(String author) throws Exception {
        Document doc = loadDocument();
        XPath xpath = XPathFactory.newInstance().newXPath();

        String expression = String.format("/library/book[author='%s']", author);
        NodeList nodes = (NodeList) xpath.compile(expression).evaluate(doc, XPathConstants.NODESET);

        List<Book> books = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            books.add(nodeToBook((Element) nodes.item(i)));
        }

        return books;
    }

    public static List<Book> findBooksByYear(int year) throws Exception {
        Document doc = loadDocument();
        XPath xpath = XPathFactory.newInstance().newXPath();

        String expression = String.format("/library/book[year=%d]", year);
        NodeList nodes = (NodeList) xpath.compile(expression).evaluate(doc, XPathConstants.NODESET);

        List<Book> books = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            books.add(nodeToBook((Element) nodes.item(i)));
        }

        return books;
    }

    public static void deleteBook(int id) throws Exception {
        Document doc = loadDocument();
        XPath xpath = XPathFactory.newInstance().newXPath();

        String expression = String.format("/library/book[@id='%d']", id);
        Node bookNode = (Node) xpath.compile(expression).evaluate(doc, XPathConstants.NODE);

        if (bookNode != null) {
            Node parent = bookNode.getParentNode();
            parent.removeChild(bookNode);
            saveDocument(doc);
        }
    }

    //ВСПОМ МЕТОДЫ

    private static Document loadDocument() throws Exception {
        File xmlFile = new File(XML_PATH);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        if (xmlFile.exists()) {
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            return doc;
        } else {
            // Создаем новый документ
            Document doc = builder.newDocument();
            Element root = doc.createElement("library");
            doc.appendChild(root);
            return doc;
        }
    }

    private static void saveDocument(Document doc) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(XML_PATH));
        transformer.transform(source, result);
    }

    private static Book nodeToBook(Element element) {
        Book book = new Book();

        book.setId(Integer.parseInt(element.getAttribute("id")));
        book.setCopies(Integer.parseInt(element.getAttribute("copies")));
        book.setAvailable(Integer.parseInt(element.getAttribute("available")));

        book.setTitle(getTagValue(element, "title"));
        book.setAuthor(getTagValue(element, "author"));
        book.setYear(Integer.parseInt(getTagValue(element, "year")));
        book.setPrice(Double.parseDouble(getTagValue(element, "price")));
        book.setCategory(getTagValue(element, "category"));

        return book;
    }

    private static String getTagValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return "";
    }

    private static Element createElement(Document doc, String name, String value) {
        Element element = doc.createElement(name);
        element.setTextContent(value);
        return element;
    }
}
