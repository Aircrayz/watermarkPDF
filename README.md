# Tool to create a watermark / background via text, image or pdf in an existing pdf

iTextPDF ist used in this project.
Please refer to: https://itextpdf.com for more information about this lib.

## Download the jar and run it:

```
  java -jar watermarkPDF -i:<inputFile> -w:<watermark>
```

Following params can be provided:

|Param:|Description:|Example:|
|----|----|----|
|-help|print this table||
|-i:|Path of the original pdf (has to be provided)|-i:C:/Documents/Test.pdf|
|-w:|Watermark object, can be either a path to an image or pdf or a string (has to be provided)(if anything other than pdf, the type must be provided)|-w:C:/Documents/Watermark.pdf|
|-o:|Path of the output file. If not provided the input file will be overwritten|-o:C:/Documents/Watermarked.pdf|
|-t:|Type of the Watermarked object. Has to be one of 'pdf', 'img' or 'text' (if not provided, it assumes to be pdf)|-t:img|
