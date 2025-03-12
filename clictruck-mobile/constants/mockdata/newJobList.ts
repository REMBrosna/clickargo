interface NewListData {
    jobId: string;
    jobType: string;
    locFrom: string;
    locTo: string;
    timePickUp: string;
    timeDropOff: string;
    time1?: string | null;
    time2?: string | null;
    time3?: string | null;
    time4?: string | null;
}
const NEW_DATA: NewListData[] = [
    {
        jobId: "CKCTJ2023101900001",
        jobType: "DOMESTIC",
        locFrom:
            "The Prime Office Suite Yos Sudarso Kav. 30 Street, 7th Floor, RT.10/RW.11, Sunter Jaya, Kec. Tj. Priok, Jkt Utara, Daerah Khusus Ibukota Jakarta 14330",
        locTo: "Jl. Raya Daan Mogot No.2, RT.2/RW.4, Kalideres, Kec. Kalideres, Kota Jakarta Barat",
        timePickUp: "2023-10-02 11:50",
        timeDropOff: "2023-10-02 13:01",
        time1: "2023-10-02 09:15",
        time2: "2023-10-02 11:30",
        time3: "2023-10-02 12:45",
        time4: "2023-10-02 13:10",
    },
    {
        jobId: "CKCTJ2023102300003",
        jobType: "EXPORT",
        locFrom:
            "The Prime Office Suite Yos Sudarso Kav. 30 Street, 7th Floor, RT.10/RW.11, Sunter Jaya, Kec. Tj. Priok, Jkt Utara, Daerah Khusus Ibukota Jakarta 14330",
        locTo: "Jl. Raya Daan Mogot No.2, RT.2/RW.4, Kalideres, Kec. Kalideres, Kota Jakarta Barat",
        timePickUp: "2023-10-02 11:51",
        timeDropOff: "2023-10-02 13:02",
        time1: "2023-10-02 09:15",
        time2: "2023-10-02 11:30",
        time3: "2023-10-02 12:45",
        time4: "2023-10-02 13:10",
    },
    {
        jobId: "CKCTJ2023102300021",
        jobType: "IMPORT",
        locFrom:
            "The Prime Office Suite Yos Sudarso Kav. 30 Street, 7th Floor, RT.10/RW.11, Sunter Jaya, Kec. Tj. Priok, Jkt Utara, Daerah Khusus Ibukota Jakarta 14330",
        locTo: "Jl. Raya Daan Mogot No.2, RT.2/RW.4, Kalideres, Kec. Kalideres, Kota Jakarta Barat",
        timePickUp: "2023-10-02 11:52",
        timeDropOff: "2023-10-02 13:03",
        time1: "2023-10-02 09:15",
        time2: "2023-10-02 11:30",
        time3: "2023-10-02 12:45",
        time4: "2023-10-02 13:10",
    },
];

export {NEW_DATA, NewListData}