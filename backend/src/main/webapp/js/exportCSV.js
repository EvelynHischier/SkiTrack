
function CSVGenerator(content) {
// inspired by http://www.hybridplanet.co.in/tutorial/javascript/how-to-create-csv-or-excel-file-from-json-via-javascript

    var CSV = '';
    var date = new Date();

    CSV = content;

    if (CSV == '') {
        alert("Invalid data");
        return;
    }

    // replacing the placeholder for line break
    CSV = CSV.replace(/#/g, "\r\n");

    //Generate a file name
    var fileName = "SkiTrack_GPSData_";

    fileName += date.getFullYear().toString() + "_"
    			+ date.getMonth().toString() + "_"
    			+ date.getDate().toString();


    //Initialize file format you want csv or xls
    var uri = 'data:text/csv;charset=utf-8,' + escape(CSV);

    // Now the little tricky part.
    // you can use either>> window.open(uri);
    // but this will not work in some browsers
    // or you will not get the correct file extension

    //this trick will generate a temp <a /> tag
    var link = document.createElement("a");
    link.href = uri;

    //set the visibility hidden so it will not effect on your web-layout
    link.style = "visibility:hidden";
    link.download = fileName  + ".csv";

    //this part will append the anchor tag and remove it after automatic click
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}
