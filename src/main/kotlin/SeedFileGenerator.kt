import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.monitorjbl.xlsx.StreamingReader
import org.apache.poi.xssf.usermodel.XSSFWorkbook


class SeedFileGenerator(
    private val commonSectionDump: String,
    private val staplesDump: String,
    private val textFieldValue: String,
) {
    private var seedFilePath = ""
    fun generateSeedRequestFile(): String {
        return if (commonSectionDump != "" && staplesDump != "" && textFieldValue != "") {
            val categories: List<String> = textFieldValue
                .replace("(, )|(\r)|(\n)".toRegex(), ",")
                .replace("(, )|([,]+)".toRegex(), ",")
                .split(",")
            println(categories)
            println(commonSectionDump)
            println(staplesDump)
            getListWithCategories(categories)
            "Successful"
        } else {
            "Some error"
        }
    }

    private fun getListWithCategories(cat: List<String>) {
        val listOfCategories = mutableListOf<MutableList<String>>()
        val staplesDumpFile = FileInputStream(File(staplesDump))
        val workBook = StreamingReader.builder()
            .rowCacheSize(100) // number of rows to keep in memory (defaults to 10)
            .bufferSize(4096) // buffer size to use when reading InputStream to file (defaults to 1024)
            .open(staplesDumpFile) // Input

        val fullDump = mutableListOf<MutableList<String>>()
        workBook.getSheetAt(0).forEach { row ->
            val tmp = mutableListOf<String>()
            row.forEach { cell ->
                tmp.add(cell.stringCellValue)
            }
            fullDump.add(tmp)
        }

        cat.forEach {
            fullDump.forEach { row ->
                val tmp = mutableListOf<String>()
                if (row[3] == it) {
                    row.forEach { cell ->
                        tmp.add(cell)
                    }
                    listOfCategories.add(tmp)
                }
            }
        }
        generateTable(listOfCategories, cat)
    }

    private fun generateTable(listOfCategories: MutableList<MutableList<String>>, cat: List<String>) {
        val tt = mutableMapOf<String, MutableList<String>>()
        var tmp: String
        var tmpCatId = ""
        tt["filter"] = mutableListOf(
            "{L=0}",
            "{I=0}",
            "NAME",
            "EXPRESSION",
            "EXPRESSION_STATUS",
            "#8",
            "#9",
            "#12",
            "#45",
            "#10",
            "#13",
            "#14",
            "#48"
        )
        cat.forEach {
            listOfCategories.forEach { row ->
                tmp = ""
                if (row[3] == it) {
                    if (row[10] == "List Of Values") {
                        tmp = if (tt[row[3] + row[9]] == null) {
                            println(row[3])
                            row[14]
                        } else {
                            tt[row[3] + row[9]]!![10] + "|" + row[14]
                        }
                    }
                    // create seed table for category
                    tmpCatId = row[3] + row[9] + "|" + row[3] + "|" + row[4]
                    tt[row[3] + row[9]] = mutableListOf(
                        row[3],
                        row[4],
                        row[8],
                        "Add(R(\"cnet_common_${row[9]}\"));",
                        "ready",
                        row[13].replace("N", "OPTIONAL").replace("Y", "REQUIRED"),
                        row[10].replace("number", "DECIMAL")
                            .replace("text", "TEXT")
                            .replace("List Of Values", "LIST"),
                        row[9],
                        "1",
                        "0",
                        tmp,
                        row[16],
                        if (tmp != "") (tmp.count { it == '|' } + 1).toString() else ""
                    )
                }
            }
            if (tmpCatId != "") {
                tt[tmpCatId.split("|").first()] = mutableListOf(
                    tmpCatId.split("|")[1],
                    tmpCatId.split("|")[2],
                    "Short Name",
                    "", "",
                    "REQUIRED",
                    "TEXT",
                    "CNET_Specific_Short_Name",
                    "1", "0", "", "", ""
                )
                tmpCatId = ""
            }
        }
        // removing not needed items
        listOfCategories.forEach {
            tt.remove("${it[3]}SP-351756")
        }
        addCommonSectionItems(tt)
        writeToFile(tt)
    }

    private fun addCommonSectionItems(table: MutableMap<String, MutableList<String>>) {

        val listOfCommonItems = getCSVColumnsByIndex(commonSectionDump)
        val tmpMap = mutableMapOf<String, MutableList<String>>()
        for ((k, v) in table) {
            if (!listOfCommonItems.contains("cnet_common_${v[7]}") && k != "filter" && v[7] != "CNET_Specific_Short_Name") {
                println(k)
                tmpMap["COMMON_${v[7]}"] = mutableListOf(
                    "COMMON",
                    "COMMON_SECTIONS",
                    v[2],
                    "",
                    "",
                    "OPTIONAL",
                    v[6],
                    "cnet_common_${v[7]}",
                    v[8],
                    v[9],
                    v[10],
                    v[11],
                    v[12]
                )
            }
        }
        for ((key, value) in tmpMap) {
            table[key] = value
        }
    }

    private fun getCSVColumnsByIndex(filepath: String, indexOfColumns: Int = 47): MutableSet<String> {
        val list = mutableSetOf<String>()
        csvReader().open(filepath) {
            readAllAsSequence().forEach { row ->
                list.add(row[indexOfColumns])
            }
        }
        return list
    }

    private fun writeToFile(rowsToWrite: MutableMap<String, MutableList<String>>) {
        val myWorkBook = XSSFWorkbook()
        val myWorkList = myWorkBook.createSheet("seed_request")
        var row = 0
        var column = 0
        myWorkList.createRow(0)
        rowsToWrite.forEach { (_, value) ->
            myWorkList.createRow(row)
            value.forEach {
                myWorkList.getRow(row).createCell(column).setCellValue(it)
                column++
            }
            row++
            column = 0
        }
        val file = File("seed_test.xlsx")
        val output = FileOutputStream(file)
        seedFilePath = file.absolutePath
        myWorkBook.write(output)
    }
}