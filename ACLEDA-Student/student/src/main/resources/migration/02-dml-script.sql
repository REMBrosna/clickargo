/**/
INSERT INTO `t_notification_template` (`NOT_ID`, `NOT_CHANNEL_TYPE`, `NOT_CONTENT_TYPE`, `NOT_SUBJECT`, `NOT_CONTENT`, `NOT_DESC`, `NOT_REC_STATUS`) VALUES (1, 'EMAIL', 'HTML', 'Over return date', '<html>\r\n   <head>\r\n      <meta http-equiv=Content-Type content=\"text/html; charset=windows-1252\">\r\n      <meta name=Generator content=\"Microsoft Word 15 (filtered)\">\r\n   </head>\r\n   <body lang=EN-SG link=\"#0563C1\" vlink=\"#954F72\">\r\n      <div class=WordSection1>\r\n         <p class=MsoNormal><span style=\"font-family:\"Arial\",sans-serif\">Dear :userName, </span></p>\r\n         <p class=MsoNormal><span style=\"font-family:\"Arial\",sans-serif\">&nbsp;</span></p>\r\n         <p class=MsoNormal><span style=\"font-family:\"Arial\",sans-serif\">Your Borrow :appId has been over return date. &nbsp; </span></p>\r\n		 <p class=MsoNormal><span style=\"font-family:\"Arial\",sans-serif\">Your Borrow Date :borrowDate &nbsp; </span></p>\r\n		 <p class=MsoNormal><span style=\"font-family:\"Arial\",sans-serif\">Your Return Date :returnDate &nbsp; </span></p>\r\n         <p class=MsoNormal><span style=\"font-family:\"Arial\",sans-serif\">&nbsp;</span></p>\r\n         <p class=MsoNormal><span style=\"font-family:\"Arial\",sans-serif\">Thanks and Best Regards, </span></p>\r\n         <p class=MsoNormal><span style=\"font-family:\"Arial\",sans-serif\">&nbsp;</span></p>\r\n         <p class=MsoNormal><span style=\"font-family:\"Arial\",sans-serif\">Library Support Team</span></p>\r\n      </div>\r\n   </body>\r\n</html>', 'Over return date template', 'A');
INSERT INTO `t_notification_template` (`NOT_ID`, `NOT_CHANNEL_TYPE`, `NOT_CONTENT_TYPE`, `NOT_SUBJECT`, `NOT_CONTENT`, `NOT_DESC`, `NOT_REC_STATUS`) VALUES (2, 'TLG', 'TEXT', 'Over return date', 'Dear :userName, \r\n\r\nYour borrow :appId has been over return date.\r\nBorrow Date :borrowDate\r\nReturn Date :returnDate\r\n\r\nThanks and Best Regards,\r\n\r\nLibrary Support Team', 'Over return date template', 'A');

/**/
INSERT INTO `t_system_param` (`SYP_KEY`, `SYP_VAL`, `SYP_DESC`, `SYP_REC_STATUS`) VALUES ('NOTIFICATION_RETRY', '4', NULL, 'A');
INSERT INTO `t_system_param` (`SYP_KEY`, `SYP_VAL`, `SYP_DESC`, `SYP_REC_STATUS`) VALUES ('PENALTY_UNIT_AMOUNT', '500', NULL, 'A');

/**/
INSERT INTO `t_notification_app` (`NOA_ID`, `NOA_APPLICATION_TYPE`, `NOA_ACTION`, `NOA_EMAIL_TEMPLATE`, `NOA_EMAIL_REQUIRED`, `NOA_TELEGRAM_TEMPLATE`, `NOA_TELEGRAM_REQUIRED`, `NOA_REC_STATUS`, `NOA_DT_CREATE`, `NOA_DT_LUPD`, `NOA_UID_CREATE`, `NOA_UID_LUPD`) VALUES (1, 'BRW', 'OVR', 1, 'Y', 2, 'Y', 'A', '2022-11-27 15:41:53', '2022-11-27 15:41:56', NULL, NULL);

/**/
INSERT INTO `T_REPORT_SERVICE_CONFIG` (`RES_ID`, `RES_VAL`, `RES_SERVICE_NAME`, `RES_DESC`, `RES_REC_STATUS`) VALUES (1, 'pdf', 'BookReportServiceImpl', NULL, 'A');
INSERT INTO `T_REPORT_SERVICE_CONFIG` (`RES_ID`, `RES_VAL`, `RES_SERVICE_NAME`, `RES_DESC`, `RES_REC_STATUS`) VALUES (2, 'excel', 'BookReportServiceImpl', NULL, 'A');

/**/
INSERT INTO `T_REPORT_TEMPLATE_CONFIG` (`RET_ID`, `RET_SERVICE`, `RET_TEMPLATE_TYPE`, `RET_SUB_TEMPLATE`, `RET_TEMPLATE_PATH`, `RET_TEMPLATE_LOGO`, `RET_REC_STATUS`) VALUES (1, 1, 'MAIN', NULL, 'templates/jasper/book/BookReport.jrxml', NULL, 'A');
INSERT INTO `T_REPORT_TEMPLATE_CONFIG` (`RET_ID`, `RET_SERVICE`, `RET_TEMPLATE_TYPE`, `RET_SUB_TEMPLATE`, `RET_TEMPLATE_PATH`, `RET_TEMPLATE_LOGO`, `RET_REC_STATUS`) VALUES (2, 1, 'SUB', 'subReportPath', 'templates/jasper/book/SubBook.jasper', NULL, 'A');
INSERT INTO `T_REPORT_TEMPLATE_CONFIG` (`RET_ID`, `RET_SERVICE`, `RET_TEMPLATE_TYPE`, `RET_SUB_TEMPLATE`, `RET_TEMPLATE_PATH`, `RET_TEMPLATE_LOGO`, `RET_REC_STATUS`) VALUES (3, 2, 'MAIN', NULL, 'templates/excel/book/BookReport.xlsx', NULL, 'A');

ALTER TABLE t_std_user ADD CONSTRAINT unique_email UNIQUE (email);