import Mock from "../mock";

export const MOCK_SHIPMENT_TYPE = [{ value: "EXPORT", desc: "EXPORT" }, { value: "IMPORT", desc: "IMPORT" }];

export const MOCK_JOBS_STATUS = {
    IN_PROGRESS: { code: "IN_PROGRESS", name: "IN PROGRESS" },
    SUBMITTED: { code: "SUBMITTED", name: "SUBMITTED" },
    COMPLETED: { code: "COMPLETED", name: "COMPLETED" },
    NEW: { code: "NEW", name: "NEW" },
    REJECTED: { code: "REJECTED", name: "REJECTED" },
    CANCELLED: { code: "CANCELLED", name: "CANCELLED" },
    DRAFT: { code: "DRAFT", name: "DRAFT" },
    PAID: { code: "PAID", name: "PAID" },
    PAYMENT_VERIFIED: { code: "PAYMENT_VERIFIED", name: "PAYMENT VERIFIED" },
}

export const MOCK_DOC_TYPE = [
    { value: "BL", desc: "BILL OF LADING" }]

const generateID = (prefix) => {
    var date = new Date();
    var y = date.toLocaleDateString("default", { year: "numeric" });
    var m = date.toLocaleDateString("default", { month: "2-digit" });
    var d = date.toLocaleDateString("default", { day: "2-digit" });
    return prefix + y + m + d + parseInt(Math.random() * 100);
}

export const newJobDb = {
    jobId: generateID('CKDOJ'),
    shipmentType: "IMPORT",
    vesselNo: "",
    refNo: "",
    status: MOCK_JOBS_STATUS.NEW.code,
    authoriserUserId: "",
    authorizedParty: {
        isAuthorized: true
    },
    startDate: null,
    expiryDate: null,
    docNo: "",
    documentType: "BL",
    documentFile: []
};

export const jobsDb = {
    list: [
        {
            jobId: "CKDOJ2022081051312",
            blNo: "BL63234124",
            shipmentType: "IMPORT",
            vesselNo: "VN1222",
            refNo: "REF123",
            authoriserUserId: "CARGO_OWNER_USER",
            docNo: "DN1223",
            documentType: "BL",

            authorizedParty: {
                accnId: "YUSEN",
                accnName: "Yusen Logistics",
                isAuthorized: true
            },
            startDate: "2022-08-10",
            expiryDate: "2022-08-12",
            dtSubmitted: "2022-08-10 15:30:00",
            status: MOCK_JOBS_STATUS.IN_PROGRESS.code

        },

        {
            jobId: "CKDOJ2022081062343",
            blNo: "BL6175463",
            shipmentType: "IMPORT",
            vesselNo: "VN1222",
            refNo: "REF123",
            authoriserUserId: "CARGO_OWNER_USER",
            docNo: "DN1223",
            documentType: "BL",
            authorizedParty: {
                accnId: "PTRINITY",
                accnName: "PT. Trinity Omega PerkasaTay",
                isAuthorized: true
            },
            startDate: "2022-08-10",
            expiryDate: "2022-08-12",
            dtSubmitted: "2022-09-20 15:30:00",
            status: MOCK_JOBS_STATUS.SUBMITTED.code,
            attchId: 'DOC123'
        },
        {
            jobId: "CKDOJ2022081073452",
            blNo: "S16845342",
            shipmentType: "IMPORT",
            vesselNo: "VN1222",
            refNo: "REF123",
            authoriserUserId: "CARGO_OWNER_USER",
            docNo: "DN1223",
            documentType: "BL",
            authorizedParty: {
                accnId: "PTERA",
                accnName: "PT. Tera Forwarders",
                isAuthorized: true
            },
            startDate: "2022-08-10",
            expiryDate: "2022-08-12",
            dtSubmitted: "2022-09-22 15:30:00",
            status: MOCK_JOBS_STATUS.COMPLETED.code,
            attchId: 'DOC125'
        },
        {
            jobId: "CKDOJ2022081092343",
            blNo: "S734323",
            shipmentType: "IMPORT",
            vesselNo: "VN1222",
            refNo: "REF123",
            authoriserUserId: "CARGO_OWNER_USER",
            docNo: "DN1223",
            documentType: "BL",
            authorizedParty: {
                accnId: "PPBOY",
                accnName: "Paperboy Logistics",
                isAuthorized: true
            },
            startDate: "2022-08-10",
            expiryDate: "2022-08-12",
            dtSubmitted: null,
            status: MOCK_JOBS_STATUS.NEW.code
        },
        {
            jobId: "CKDOJ2022081053232",
            blNo: "S16734323",
            shipmentType: "IMPORT",
            vesselNo: "VN1222",
            refNo: "REF123",
            authoriserUserId: "CARGO_OWNER_USER",
            docNo: "DN1223",
            documentType: "BL",
            authorizedParty: {
                accnId: "YUSEN",
                accnName: "Yusen Logistics",
                isAuthorized: true
            },
            startDate: "2022-08-10",
            expiryDate: "2022-08-12",
            dtSubmitted: "2022-09-21 16:38:00",
            status: MOCK_JOBS_STATUS.REJECTED.code
        },
        {
            jobId: "CKDOJ2022081063438",
            blNo: "S16734323",
            shipmentType: "IMPORT",
            vesselNo: "VN1222",
            refNo: "REF123",
            authoriserUserId: "CARGO_OWNER_USER",
            docNo: "DN1223",
            documentType: "BL",
            authorizedParty: {
                accnId: "PTRINITY",
                accnName: "PT. Trinity Omega PerkasaTay",
                isAuthorized: true
            },
            startDate: "2022-08-10",
            expiryDate: "2022-08-12",
            dtSubmitted: "2022-09-21 16:38:10",
            status: MOCK_JOBS_STATUS.CANCELLED.code,
            attchId: 'DOC124'
        },

    ],
};

export const doClaimJobsDb = {
    list: [
        {
            jobId: "CKDOJ2022081092343",
            noOfBl: 4,
            shipmentType: "IMPORT",
            vesselNo: "VN1222",
            refNo: "REF123",
            authoriserUserId: "CARGO_OWNER_USER",
            docNo: "DN1223",
            documentType: "BL",
            authorizedParty: {
                accnId: "YUSEN",
                accnName: "Yusen Logistics",
                isAuthorized: true
            },
            startDate: "2022-08-10",
            expiryDate: "2022-08-12",
            dtSubmitted: "2022-09-21 16:38:10",
            status: MOCK_JOBS_STATUS.DRAFT.code,
            blList: [
                {
                    blNo: 'BL6624531',
                    type: 'IMPORT',
                    authoriser: 'Good Tyre Pte Ltd Logistics',
                    dtSubmit: '2022-08-15 15:30:00'
                },
                {
                    blNo: 'BL7234122',
                    type: 'IMPORT',
                    authoriser: 'Good Tyre Pte Ltd Logistics',
                    dtSubmit: '2022-08-15 15:30:00'
                },
                {
                    blNo: 'BL2315155',
                    type: 'IMPORT',
                    authoriser: 'PT. Cargo Owner Wong',
                    dtSubmit: '2022-08-15 15:30:00'
                },
                {
                    blNo: 'BL9834532',
                    type: 'IMPORT',
                    authoriser: 'PT. Cargo Owner Wong',
                    dtSubmit: '2022-08-15 15:30:00'
                }
            ]
        },
        {
            jobId: "CKDOJ2022081051312",
            noOfBl: 3,
            shipmentType: "IMPORT",
            vesselNo: "VN1222",
            refNo: "REF123",
            authoriserUserId: "CARGO_OWNER_USER",
            docNo: "DN1223",
            documentType: "BL",
            authorizedParty: {
                accnId: "PTRINITY",
                accnName: "PT. Trinity Omega PerkasaTay",
                isAuthorized: true
            },
            startDate: "2022-08-10",
            expiryDate: "2022-08-12",
            dtSubmitted: "2022-08-10 15:30:00",
            status: MOCK_JOBS_STATUS.NEW.code,
            blList: [
                {
                    blNo: 'BL6624781',
                    type: 'IMPORT',
                    authoriser: 'PT. Cargo Owner Wong',
                    dtSubmit: '2022-08-15 15:30:00'
                },
                {
                    blNo: 'BL7234329',
                    type: 'IMPORT',
                    authoriser: 'Good Tyre Pte Ltd Logistics',
                    dtSubmit: '2022-08-15 15:30:00'
                },
                {
                    blNo: 'BL9237612',
                    type: 'IMPORT',
                    authoriser: 'PT. Cargo Owner Wong',
                    dtSubmit: '2022-08-15 15:30:00'
                }
            ]
        },
        {
            jobId: "CKDOJ2022081062343",
            noOfBl: 5,
            shipmentType: "IMPORT",
            vesselNo: "VN1222",
            refNo: "REF123",
            authoriserUserId: "CARGO_OWNER_USER",
            docNo: "DN1223",
            documentType: "BL",
            authorizedParty: {
                accnId: "PPBOY",
                accnName: "Paperboy Logistics",
                isAuthorized: true
            },
            startDate: "2022-08-10",
            expiryDate: "2022-08-12",
            dtSubmitted: "2022-09-20 08:12:00",
            status: MOCK_JOBS_STATUS.SUBMITTED.code,
            blList: [
                {
                    blNo: 'BL6624531',
                    type: 'IMPORT',
                    authoriser: 'Good Tyre Pte Ltd Logistics',
                    dtSubmit: '2022-08-15 15:30:00'
                },
                {
                    blNo: 'BL7234122',
                    type: 'IMPORT',
                    authoriser: 'Good Tyre Pte Ltd Logistics',
                    dtSubmit: '2022-08-15 15:30:00'
                },
                {
                    blNo: 'BL2315155',
                    type: 'IMPORT',
                    authoriser: 'PT. Cargo Owner Wong',
                    dtSubmit: '2022-08-15 15:30:00'
                },
                {
                    blNo: 'BL9834532',
                    type: 'IMPORT',
                    authoriser: 'PT. Cargo Owner Wong',
                    dtSubmit: '2022-08-15 15:30:00'
                },
                {
                    blNo: 'BL923112',
                    type: 'IMPORT',
                    authoriser: 'PT. Cargo Owner Wong',
                    dtSubmit: '2022-08-15 15:30:00'
                }
            ]
        },
        {
            jobId: "CKDOJ2022081073452",
            noOfBl: 5,
            shipmentType: "IMPORT",
            vesselNo: "VN1222",
            refNo: "REF123",
            authoriserUserId: "CARGO_OWNER_USER",
            docNo: "DN1223",
            documentType: "BL",
            authorizedParty: {
                accnId: "PTERA",
                accnName: "PT. Tera Forwarders",
                isAuthorized: true
            },
            startDate: "2022-08-10",
            expiryDate: "2022-08-12",
            dtSubmitted: "2022-09-22 15:10:34",
            status: MOCK_JOBS_STATUS.COMPLETED.code,
            blList: [
                {
                    blNo: 'BL6624531',
                    type: 'IMPORT',
                    authoriser: 'Good Tyre Pte Ltd Logistics',
                    dtSubmit: '2022-08-15 15:30:00'
                },
                {
                    blNo: 'BL7234122',
                    type: 'IMPORT',
                    authoriser: 'Good Tyre Pte Ltd Logistics',
                    dtSubmit: '2022-08-15 15:30:00'
                },
                {
                    blNo: 'BL2315155',
                    type: 'IMPORT',
                    authoriser: 'PT. Cargo Owner Wong',
                    dtSubmit: '2022-08-15 15:30:00'
                },
                {
                    blNo: 'BL9834532',
                    type: 'IMPORT',
                    authoriser: 'PT. Cargo Owner Wong',
                    dtSubmit: '2022-08-15 15:30:00'
                },
                {
                    blNo: 'BL923112',
                    type: 'IMPORT',
                    authoriser: 'PT. Cargo Owner Wong',
                    dtSubmit: '2022-08-15 15:30:00'
                }
            ]
        },
        {
            jobId: "CKDOJ2022081269212",
            noOfBl: 3,
            shipmentType: "IMPORT",
            vesselNo: "VN1222",
            refNo: "REF123",
            authoriserUserId: "CARGO_OWNER_USER",
            docNo: "DN1223",
            documentType: "BL",
            authorizedParty: {
                accnId: "PTERA",
                accnName: "PT. Tera Forwarders",
                isAuthorized: true
            },
            startDate: "2022-09-15",
            expiryDate: "2022-12-24",
            dtSubmitted: "2022-09-16 07:10:12",
            status: MOCK_JOBS_STATUS.PAID.code,
            blList: [
                {
                    blNo: 'BL6624531',
                    type: 'IMPORT',
                    authoriser: 'Good Tyre Pte Ltd Logistics',
                    dtSubmit: '2022-08-15 15:30:00'
                },
                {
                    blNo: 'BL7234122',
                    type: 'IMPORT',
                    authoriser: 'Good Tyre Pte Ltd Logistics',
                    dtSubmit: '2022-08-15 15:30:00'
                },
                {
                    blNo: 'BL923112',
                    type: 'IMPORT',
                    authoriser: 'PT. Cargo Owner Wong',
                    dtSubmit: '2022-08-15 15:30:00'
                }
            ]
        },
        {
            jobId: "CKDOJ2022081593822",
            noOfBl: 1,
            shipmentType: "IMPORT",
            vesselNo: "VN1222",
            refNo: "REF123",
            authoriserUserId: "CARGO_OWNER_USER",
            docNo: "DN1223",
            documentType: "BL",
            authorizedParty: {
                accnId: "PTERA",
                accnName: "PT. Tera Forwarders",
                isAuthorized: true
            },
            startDate: "2022-09-21",
            expiryDate: "2022-12-15",
            dtSubmitted: "2022-09-22 12:34:55",
            status: MOCK_JOBS_STATUS.PAYMENT_VERIFIED.code,
            invoice: " ",
            blList: [
                {
                    blNo: 'BL8310305',
                    type: 'IMPORT',
                    authoriser: 'Good Tyre Pte Ltd Logistics',
                    dtSubmit: '2022-08-15 15:30:00'
                },
            ]
        },
    ],
};

export const jobsAudit = {
    list: [
        { audtId: '1', audtEvent: 'JOB_CREATED', audtRemarks: '', audtTimestamp: '2022-09-20 16:18:03', audtUid: 'USER_ID', audtUname: 'USER 01' },
        { audtId: '2', audtEvent: 'JOB_SUBMITTED', audtRemarks: '', audtTimestamp: '2022-09-20 16:18:03', audtUid: 'USER_ID', audtUname: 'USER 01' },
        { audtId: '3', audtEvent: 'JOB_IN_PROGRESS', audtRemarks: '', audtTimestamp: '2022-09-20 16:18:03', audtUid: 'USER_02', audtUname: 'USER 02' },
        { audtId: '4', audtEvent: 'JOB_COMPLETED', audtRemarks: '', audtTimestamp: '2022-09-20 16:18:03', audtUid: 'USER_ID', audtUname: 'USER 02' },
    ]
};


Mock.onPost("/api/jobs/new").reply((config) => {
    return [200, newJobDb];
});

//api/jobs/all/list
Mock.onGet(/\api\/jobs\/all\/list\/?.*/).reply((config) => {
    return [200, jobsDb.list];
});

Mock.onGet(/\/api\/jobs\/details\/\w+/).reply((config) => {
    const id = config.url.split("/")[4];
    const response = jobsDb.list.find((job) => job.jobId === id);
    return [200, response];
});

// Claim DO
Mock.onGet(/\/api\/jobs\/mockdetails\/\w+/).reply((config) => {
    const id = config.url.split("/")[4];
    const response = doClaimJobsDb.list.find((job) => job.jobId === id);
    return [200, response];
});

Mock.onGet(/\/api\/jobs\/authLetters\/\w+/).reply((config) => {
    const id = config.url.split("/")[4];

    const response = authorisationLetters.filter((file) => file.attReferenceid === id);
    console.log("response authLetters", response);
    return [200, response];
});

Mock.onGet(/\/api\/jobs\/authLetter\/download\/\w+/).reply((config) => {
    const fileId = config.url.split("/")[5];
    const response = authorisationLetters.find((file) => file.attId === fileId);
    return [200, response];
});

Mock.onPut(/\/api\/jobs\/\w+/).reply((config) => {
    let id = config.url.split("/")[3];
    let jobData = JSON.parse(config.data);

    jobsDb.list.forEach((element, idx) => {
        if (element.jobId === jobData?.jobId) {
            jobsDb.list[idx] = jobData;
            return element;
        }
    });

    console.log("list", jobsDb);
    return [200, jobData];
});

Mock.onPut(/\/api\/job\/attach\/\w+/).reply((config) => {
    let refId = config.url.split("/")[4];
    let attchData = JSON.parse(config.data);
    const index = authorisationLetters.findIndex((file) => file.attReferenceid === refId);

    const randomId = Math.random();
    let data = { ...attchData };
    if (index < 0) {
        //not found insert
        data = { ...data, attId: randomId.toString(), attDtCreate: new Date() };
        authorisationLetters.push(data);
        jobsDb.list.forEach(job => {
            if (job.jobId === refId) {
                job.attchId = randomId;
            }
        });
    } else {
        authorisationLetters[index].attData = attchData?.attData;
        authorisationLetters[index].attName = attchData?.attName;
    }


    return [200, data];
});

Mock.onPost("/api/jobs/delete").reply((config) => {
    let user = JSON.parse(config.data);
    let index = { i: 0 };
    jobsDb.list.forEach((element) => {
        if (element.id === user.id) {
            return [200, jobsDb.list.splice(index.i, 1)];
        }
        index.i++;
    });
    return [200, jobsDb.list];
});



Mock.onPost("/api/jobs/add").reply((config) => {
    let user = JSON.parse(config.data);
    jobsDb.list.push(user);
    return [200, jobsDb.list];
});



export const authorisationLetters = [
    {
        attId: 'DOC123', attSeq: '1',
        attType: 'BILL OF LADING',
        attReferenceid: 'CKDOJ2022081062343',
        attName: 'CKDOJ2022081062343_BL.pdf',
        attDesc: 'Bill of Lading CKDOJ2022081062343',
        attUid: "CARGO_OWNER_USER",
        attData: "JVBERi0xLjMKJcTl8uXrp/Og0MTGCjMgMCBvYmoKPDwgL0ZpbHRlciAvRmxhdGVEZWNvZGUgL0xlbmd0aCAxNjAgPj4Kc3RyZWFtCngBdY87C8JAEIT7/IrxvRG83K130bSKjV1gO2MVsBBShPx/MNnTKIpscd/NPphpUaJFduwc6g5Wq6t7yRr28T+A2yNnZwpG3eAgCHGyf5gDQl5AmiQTYTjIDRfQJMXGGg+aKgTQ7AfmqjjQQmELWipwQmNr3FpVpM3Pk1X6ktYprpAzTqKB/rhPvt37HZvCWv+MgHeEwYHc473yAdUrOMUKZW5kc3RyZWFtCmVuZG9iagoxIDAgb2JqCjw8IC9UeXBlIC9QYWdlIC9QYXJlbnQgMiAwIFIgL1Jlc291cmNlcyA0IDAgUiAvQ29udGVudHMgMyAwIFIgL01lZGlhQm94IFswIDAgNTk1IDg0Ml0KPj4KZW5kb2JqCjQgMCBvYmoKPDwgL1Byb2NTZXQgWyAvUERGIC9UZXh0IF0gL0NvbG9yU3BhY2UgPDwgL0NzMSA1IDAgUiA+PiAvRm9udCA8PCAvVFQyIDcgMCBSCj4+ID4+CmVuZG9iago4IDAgb2JqCjw8IC9OIDMgL0FsdGVybmF0ZSAvRGV2aWNlUkdCIC9MZW5ndGggMjYxMiAvRmlsdGVyIC9GbGF0ZURlY29kZSA+PgpzdHJlYW0KeAGdlndUU9kWh8+9N73QEiIgJfQaegkg0jtIFQRRiUmAUAKGhCZ2RAVGFBEpVmRUwAFHhyJjRRQLg4Ji1wnyEFDGwVFEReXdjGsJ7601896a/cdZ39nnt9fZZ+9917oAUPyCBMJ0WAGANKFYFO7rwVwSE8vE9wIYEAEOWAHA4WZmBEf4RALU/L09mZmoSMaz9u4ugGS72yy/UCZz1v9/kSI3QyQGAApF1TY8fiYX5QKUU7PFGTL/BMr0lSkyhjEyFqEJoqwi48SvbPan5iu7yZiXJuShGlnOGbw0noy7UN6aJeGjjAShXJgl4GejfAdlvVRJmgDl9yjT0/icTAAwFJlfzOcmoWyJMkUUGe6J8gIACJTEObxyDov5OWieAHimZ+SKBIlJYqYR15hp5ejIZvrxs1P5YjErlMNN4Yh4TM/0tAyOMBeAr2+WRQElWW2ZaJHtrRzt7VnW5mj5v9nfHn5T/T3IevtV8Sbsz55BjJ5Z32zsrC+9FgD2JFqbHbO+lVUAtG0GQOXhrE/vIADyBQC03pzzHoZsXpLE4gwnC4vs7GxzAZ9rLivoN/ufgm/Kv4Y595nL7vtWO6YXP4EjSRUzZUXlpqemS0TMzAwOl89k/fcQ/+PAOWnNycMsnJ/AF/GF6FVR6JQJhIlou4U8gViQLmQKhH/V4X8YNicHGX6daxRodV8AfYU5ULhJB8hvPQBDIwMkbj96An3rWxAxCsi+vGitka9zjzJ6/uf6Hwtcim7hTEEiU+b2DI9kciWiLBmj34RswQISkAd0oAo0gS4wAixgDRyAM3AD3iAAhIBIEAOWAy5IAmlABLJBPtgACkEx2AF2g2pwANSBetAEToI2cAZcBFfADXALDIBHQAqGwUswAd6BaQiC8BAVokGqkBakD5lC1hAbWgh5Q0FQOBQDxUOJkBCSQPnQJqgYKoOqoUNQPfQjdBq6CF2D+qAH0CA0Bv0BfYQRmALTYQ3YALaA2bA7HAhHwsvgRHgVnAcXwNvhSrgWPg63whfhG/AALIVfwpMIQMgIA9FGWAgb8URCkFgkAREha5EipAKpRZqQDqQbuY1IkXHkAwaHoWGYGBbGGeOHWYzhYlZh1mJKMNWYY5hWTBfmNmYQM4H5gqVi1bGmWCesP3YJNhGbjS3EVmCPYFuwl7ED2GHsOxwOx8AZ4hxwfrgYXDJuNa4Etw/XjLuA68MN4SbxeLwq3hTvgg/Bc/BifCG+Cn8cfx7fjx/GvyeQCVoEa4IPIZYgJGwkVBAaCOcI/YQRwjRRgahPdCKGEHnEXGIpsY7YQbxJHCZOkxRJhiQXUiQpmbSBVElqIl0mPSa9IZPJOmRHchhZQF5PriSfIF8lD5I/UJQoJhRPShxFQtlOOUq5QHlAeUOlUg2obtRYqpi6nVpPvUR9Sn0vR5Mzl/OX48mtk6uRa5Xrl3slT5TXl3eXXy6fJ18hf0r+pvy4AlHBQMFTgaOwVqFG4bTCPYVJRZqilWKIYppiiWKD4jXFUSW8koGStxJPqUDpsNIlpSEaQtOledK4tE20Otpl2jAdRzek+9OT6cX0H+i99AllJWVb5SjlHOUa5bPKUgbCMGD4M1IZpYyTjLuMj/M05rnP48/bNq9pXv+8KZX5Km4qfJUilWaVAZWPqkxVb9UU1Z2qbapP1DBqJmphatlq+9Uuq43Pp893ns+dXzT/5PyH6rC6iXq4+mr1w+o96pMamhq+GhkaVRqXNMY1GZpumsma5ZrnNMe0aFoLtQRa5VrntV4wlZnuzFRmJbOLOaGtru2nLdE+pN2rPa1jqLNYZ6NOs84TXZIuWzdBt1y3U3dCT0svWC9fr1HvoT5Rn62fpL9Hv1t/ysDQINpgi0GbwaihiqG/YZ5ho+FjI6qRq9Eqo1qjO8Y4Y7ZxivE+41smsImdSZJJjclNU9jU3lRgus+0zwxr5mgmNKs1u8eisNxZWaxG1qA5wzzIfKN5m/krCz2LWIudFt0WXyztLFMt6ywfWSlZBVhttOqw+sPaxJprXWN9x4Zq42Ozzqbd5rWtqS3fdr/tfTuaXbDdFrtOu8/2DvYi+yb7MQc9h3iHvQ732HR2KLuEfdUR6+jhuM7xjOMHJ3snsdNJp9+dWc4pzg3OowsMF/AX1C0YctFx4bgccpEuZC6MX3hwodRV25XjWuv6zE3Xjed2xG3E3dg92f24+ysPSw+RR4vHlKeT5xrPC16Il69XkVevt5L3Yu9q76c+Oj6JPo0+E752vqt9L/hh/QL9dvrd89fw5/rX+08EOASsCegKpARGBFYHPgsyCRIFdQTDwQHBu4IfL9JfJFzUFgJC/EN2hTwJNQxdFfpzGC4sNKwm7Hm4VXh+eHcELWJFREPEu0iPyNLIR4uNFksWd0bJR8VF1UdNRXtFl0VLl1gsWbPkRoxajCCmPRYfGxV7JHZyqffS3UuH4+ziCuPuLjNclrPs2nK15anLz66QX8FZcSoeGx8d3xD/iRPCqeVMrvRfuXflBNeTu4f7kufGK+eN8V34ZfyRBJeEsoTRRJfEXYljSa5JFUnjAk9BteB1sl/ygeSplJCUoykzqdGpzWmEtPi000IlYYqwK10zPSe9L8M0ozBDuspp1e5VE6JA0ZFMKHNZZruYjv5M9UiMJJslg1kLs2qy3mdHZZ/KUcwR5vTkmuRuyx3J88n7fjVmNXd1Z752/ob8wTXuaw6thdauXNu5Tnddwbrh9b7rj20gbUjZ8MtGy41lG99uit7UUaBRsL5gaLPv5sZCuUJR4b0tzlsObMVsFWzt3WazrWrblyJe0fViy+KK4k8l3JLr31l9V/ndzPaE7b2l9qX7d+B2CHfc3em681iZYlle2dCu4F2t5czyovK3u1fsvlZhW3FgD2mPZI+0MqiyvUqvakfVp+qk6oEaj5rmvep7t+2d2sfb17/fbX/TAY0DxQc+HhQcvH/I91BrrUFtxWHc4azDz+ui6rq/Z39ff0TtSPGRz0eFR6XHwo911TvU1zeoN5Q2wo2SxrHjccdv/eD1Q3sTq+lQM6O5+AQ4ITnx4sf4H++eDDzZeYp9qukn/Z/2ttBailqh1tzWibakNml7THvf6YDTnR3OHS0/m/989Iz2mZqzymdLz5HOFZybOZ93fvJCxoXxi4kXhzpXdD66tOTSna6wrt7LgZevXvG5cqnbvfv8VZerZ645XTt9nX297Yb9jdYeu56WX+x+aem172296XCz/ZbjrY6+BX3n+l37L972un3ljv+dGwOLBvruLr57/17cPel93v3RB6kPXj/Mejj9aP1j7OOiJwpPKp6qP6391fjXZqm99Oyg12DPs4hnj4a4Qy//lfmvT8MFz6nPK0a0RupHrUfPjPmM3Xqx9MXwy4yX0+OFvyn+tveV0auffnf7vWdiycTwa9HrmT9K3qi+OfrW9m3nZOjk03dp76anit6rvj/2gf2h+2P0x5Hp7E/4T5WfjT93fAn88ngmbWbm3/eE8/sKZW5kc3RyZWFtCmVuZG9iago1IDAgb2JqClsgL0lDQ0Jhc2VkIDggMCBSIF0KZW5kb2JqCjIgMCBvYmoKPDwgL1R5cGUgL1BhZ2VzIC9NZWRpYUJveCBbMCAwIDU5NSA4NDJdIC9Db3VudCAxIC9LaWRzIFsgMSAwIFIgXSA+PgplbmRvYmoKOSAwIG9iago8PCAvVHlwZSAvQ2F0YWxvZyAvUGFnZXMgMiAwIFIgPj4KZW5kb2JqCjcgMCBvYmoKPDwgL1R5cGUgL0ZvbnQgL1N1YnR5cGUgL1RydWVUeXBlIC9CYXNlRm9udCAvQUFBQUFDK0NhbGlicmkgL0ZvbnREZXNjcmlwdG9yCjEwIDAgUiAvVG9Vbmljb2RlIDExIDAgUiAvRmlyc3RDaGFyIDMzIC9MYXN0Q2hhciA0MiAvV2lkdGhzIFsgNTI1IDIyOSAyMjkKMjI2IDUyNyAzMDUgNDc5IDUyNSA1MjUgNDcxIF0gPj4KZW5kb2JqCjExIDAgb2JqCjw8IC9MZW5ndGggMjg0IC9GaWx0ZXIgL0ZsYXRlRGVjb2RlID4+CnN0cmVhbQp4AV3RTWrDMBAF4L1OoWW6CJbdNGnAGEpKwIv+ULcHsKWxEdSykOWFb983SppCF2/xSRoxGmWn+rl2NsrsPUy6oSh760ygeVqCJtnRYJ3IC2msjlelNT22XmQobtY50li7fpJlKaTMPlAyx7DKzZOZOrrjtbdgKFg3yM3XqUkrzeL9N43kolSiqqShHte9tP61HUlmqXRbG+zbuG5R9Xfic/Uk0REq8ktLejI0+1ZTaN1AolSqKs/nSpAz/7Zydano+uvRIq9KjlL7ohJlUYAIeGTegwiomTsQUapQzAcQwW7P3IMIuGceQATMmY8gAu6YRxABidmCCHhIff82yE/gUd9Go5cQMJX0H2lgPAjr6PZlfvL88JQf2fqMyQplbmRzdHJlYW0KZW5kb2JqCjEwIDAgb2JqCjw8IC9UeXBlIC9Gb250RGVzY3JpcHRvciAvRm9udE5hbWUgL0FBQUFBQytDYWxpYnJpIC9GbGFncyA0IC9Gb250QkJveCBbLTUwMyAtMzEzIDEyNDAgMTAyNl0KL0l0YWxpY0FuZ2xlIDAgL0FzY2VudCA5NTIgL0Rlc2NlbnQgLTI2OSAvQ2FwSGVpZ2h0IDYzMiAvU3RlbVYgMCAvWEhlaWdodAo0NjQgL0F2Z1dpZHRoIDUyMSAvTWF4V2lkdGggMTMyOCAvRm9udEZpbGUyIDEyIDAgUiA+PgplbmRvYmoKMTIgMCBvYmoKPDwgL0xlbmd0aDEgMTgxOTIgL0xlbmd0aCA4OTgzIC9GaWx0ZXIgL0ZsYXRlRGVjb2RlID4+CnN0cmVhbQp4AdV7eViTV9r+ebOQQAgJS1iMkGAExYCooIILiSxhFxCiCYqyi4qKKO4LdS+tXaarXe3eKV1eoq1oN9ux63Rfp+vYmXamm91m2unYKr/7vE8Oamc6vz++67uu+ZLc730/z1nec56zvSa4prunjRlYL1OzCS3Lm7qY8sr1gdJb1q6xk51ayJj2kfauxcvJTgcZnYs7N7STnXsD7LqOtqZWstnP4CkdcJAtZYNHdyxfs57sXF5BeufKlmB67k7Y8cub1gfvz96HbV/RtLyN8nu+4XZXd1swXUL7Rn9Baf/hKiHNwBqZVsmjYmaWyfYwFjVFNVnx8PSQrKxbQm88vcg043uWoFfcD3+x+QUu3ry6r/2nU6d7Q7/UT4EZylRKMmMop7vp9LuMhR346dSpA6Ffcs95L8NAqHpWrepZ1dMsh9lUzwT5A5ajepd5Ve+A3wb/Ichvgd+E/Qb4dfBr4FfBj4MfAz8KfoR5mUb1HssG6gD1sGqFdTvwBqBly1CTxAwoL7EY1ZOsEGgF1gBXAlrkfQxpt6NGidlVOw+Fxktl9kHVDiG2C3GBEL1CbBNiqxBbhNgsxCYhNgqxQYj1QqwTYq0QPUKsEWK1EKuE6BJipRArhFguRKcQy4RYKsQSITqEWCxEuxBtQrQK0SJEsxBNQjQKsUiIhUI0CLFAiPlC1AvhF8InxDwh5grhFaJOiFoh5ghRI0S1EFVCzBaiUogKIcqFKBOiVIgSIYqF8AhRJEShEAVC5AsxSwi3EC4h8oSYKcQMIaYLMU2IXCFyhJgqxBQhJguRLUSWEJOEmCjEBCEyhRgvRIYQ6UI4hRgnRJoQY4UYI0SqEClCjBbCIcQoIZKFsAthEyJJiEQhRgphFWKEEAlCxAsRJ0SsEBYhYoSIFiJKiEghzEKYhIgQwihEuBAGIcKECBVCL4ROiBAhtEJohFALoRJCEoIFhTQkxBkhTgvxsxA/CXFKiH8K8aMQ/xDiByG+F+LvQvxNiO+E+FaIb4T4WoivhDgpxJdCfCHE50J8JsSnQvxViL8I8YkQHwvxZyH+JMRHQpwQ4o9CfCjEB0K8L8R7QrwrxDtC/EGIt4V4S4g3hXhDiNeFeE2IV4V4RYiXhXhJiBeFeEGI3wvxvBDPCfGsEM8I8bQQTwlxXIjfCfGkEE8IcUyIx4V4TIhHhXhEiIeFOCrEESEGhTgsxENCPCjEISEOChEQYkAIWYgHhLhfiPuEuFeIfiHuEeK3QtwtxF1C3CnEHULcLsRtQtwqxC1CHBDiZiFuEuJGIW4Q4nohrhNivxDXCnGNEFcLcZUQVwpxhRC/EeJyIS4T4lIhLhFinxAXC3GREH1CXCjEXiH2CLFbiF1C7BRihxDbhbhAiF4htgmxVYgtQmwWYpMQG4XYIMR6IdYJsVaIHiHWCLFaiG4hVgnRJcRKIVYIsVyITiGWCbFUiCVCdAixWIh2IdqEaBWiRYhmIZqEaBRikRALhWgQYoEQ84WoF8IvhE+IeULMFcIrRJ0QtULMEaJaiCohZgtRIUS5EGVClApRIkSxEB4hioQoFKLgIH9aHlTtDCTl2fDMHEiygLaTdUEgaRqsXrK2EW0NJIXDuYWszUSbiDYSbQgkzkKW9YHEAtA6orVEPZS2hqzVRN3kXBVIzEeBLqKVRCsoy3KiTqJlgZFFyLmUaAlRB9FiovbAyEJkaSOrlaiFqJmoiaiRaBHRQirXQNYCovlE9UR+Ih/RPKK5RF6iOqJaojlENUTVRFVEs4kqiSqIyonKAtZS9KGUqCRgLYNVTOQJWMthFQWsFaBCogKifEqbReXcRC4ql0c0k2gG5ZxONI2K5xLlEE0lmkI0mSrLJsqiWiYRTSSaQJVlEo2nchlE6UROonFEaURjicZQ1alEKVTnaCIH0SiqOpnITuVsRElEiUQjiaxEIwIjZiNYCUTxgRFVsOKIYslpIYohZzRRFFEkpZmJTOSMIDIShVOagSiMKJTS9EQ6opBAQjXurg0k1IA0RGpyqsiSiJhC0hDRGSWLdJqsn4l+IjpFaf8k60eifxD9QPR9IL7ONij9PRBfC/obWd8RfUv0DaV9TdZXRCeJvqS0L4g+J+dnRJ8S/ZXoL5TlE7I+JuvPZP2J6COiE5T2R6IPyfkB0ftE7xG9S1neIesPRG8H4uahK28F4uaC3iR6g5yvE71G9CrRK5TlZaKXyPki0QtEvyd6nrI8R/QsOZ8heproKaLjRL+jnE+S9QTRMaLHKe0xokfJ+QjRw0RHiY4QDVLOw2Q9RPQg0SGig4FYFzodCMTOBw0QyUQPEN1PdB/RvUT9RPcEYrHrS7+lWu4muovS7iS6g+h2otuIbiW6hegA0c1U2U1Uy41EN1Da9UTXEe0nupYKXEPW1URXEV1JaVdQLb8hupzSLiO6lOgSon1EF1POi8jqI7qQaC/RHqLdAUsT+r4rYGkG7STaEbC0w9pOdEHA4oXVG7DgsJG2BSxTQFuJtlDxzVRuE9HGgKUVWTZQ8fVE64jWEvUQrSFaTVV3U/FVRF0BSwtqWUmVraCcy4k6iZYRLSVaQuU6iBZTy9qpeBtRK+VsIWomaiJqJFpEtJA63UAtW0A0nzpdT1X76UY+onnU3Ll0Iy/VUkdUSzSHqCYQ40bHqgMxPKxVgRi+YGcHYnaAKgMxGaAKylJOVBaIwYOEVEpWCVExOT2BmK1IKwrE7AEVBmK2gQoCMb2g/ECUBzSLyE3kIsoLROG5QJpJ1oxApB/WdKJpgUi+jnKJcgKRxbCmBiJ9oCmByHrQZErLJsoKRKbDOYlyTgxE8o5NCETyDSmTaDwVz6A7pBM5qbJxRGlU2ViiMUSpRCmBSB6l0UQOqnMU1ZlMldmpFhtREpVLJBpJZCUaQZQQMDegzviAeSEoLmBeBIolshDFEEUTRVGBSCpgJqeJKILISBROOQ2UM4ycoUR6Ih1RCOXUUk4NOdVEKiKJiLmHTM02jjOmFttpU6vtZ+ifgFPAP+H7Eb5/AD8A3wN/h/9vwHdI+xb2N8DXwFfASfi/BL5A2uewPwM+Bf4K/CVise2TiA7bx8CfgT8BH8F3AvxH4EPgA9jvg98D3gXeAf5gXGZ72zjR9hb4TWOn7Q1jqu114DXoV41O2yvAy8BLSH8RvheMy22/h34e+jnoZ41Lbc8Yl9ieNnbYnjIuth1H2d+hvieBJwD30DFcHwceAx4NX2V7JLzb9nD4atvR8DW2I8AgcBj+h4AHkXYIaQfhCwADgAw8YNhgu9+w0XafYbPtXsMWW79hq+0e4LfA3cBdwJ3AHYYM2+3g24BbUeYW8AHDMtvN0DdB3wjcAH096roOde1HXdfCdw1wNXAVcCVwBfAblLsc9V0WNtt2aViV7ZKwxbZ9YXfYLg67y7ZLnWLbqc6x7ZBybNu9vd4L+nu927xbvFv7t3gNWyTDFuuW8i2btvRveW+LOyokbLN3o3dT/0bvBu867/r+dd6jqt2sXbXLPcO7tr/Hq+mJ6VnTo/57j9TfIxX2SBN6JBXrMffYe9Tha7zd3tX93V7WXd3d2y13a6bL3Se6VaxbChscOnaw25rkAbs3dxvNnlXeld6u/pXeFe3LvUvRwCU5i70d/Yu97Tmt3rb+Vm9LTrO3KafRuyinwbuwv8G7IKfeO7+/3uvP8XnnIf/cnDqvt7/OW5tT453TX+OtypntnQ1/ZU65t6K/3FuWU+It7S/xFud4vEXoPBtpHmkfqTbzBsweiZYwq5Q/weq2nrB+Y9Uwq2w9ZlVHmUbYRqjSTAlSQVWCtDJhW8KlCWpT/MvxKnd8WrrHFPdy3B/jvo7TRLvj0sZ7WKw51h6rtvC+xVbW8b4djHUVEk+crPTVFutI9Zgskslis6iKvrZIu5lasksSk8wgtR5lDkkWm0f9KFz4sYxJ0mWszlk+qGdzymV99XxZ2iun1PKru6ZeDtkrM2/9fN+AJF3iH5BUBXVyTHlNPdm79u1jifnlcmKtL6A+cCAx318u93Ltdit6iGuGLH7nwtU9q50+90wWeSLym0i15XHzy2aVySSZTEMmlduExpsibBEqfhmKULsjJk71mIw2o4pfhozqWLcRHh7KMeHVdR6TwWZQeV2GKoPKbXAVeNyGjAmef+nnQd5PurNzzcLVTsg1TuUDyy/1cBMvpOCzeg1s/gbBZjzl11+UDfkWrcZLqYaq//Ui/wdSpP8Dbfwvb+IAwxLxzRpS7cRvmTuA7cAFQC+wDdgKbAE2A5uAjcAGYD2wDlgL9ABrgNXAKqALWAmsAJYDncAyYCmwBOgAFgPtQBvQCrQAzUAT0AgsAhYCDcACYD5QD/gBHzAPmAt4gTqgFpgD1ADVQBUwG6gEKoByoAwoBUqAYsADFAGFQAGQD8wC3IALyANmAjOA6cA0IBfIAaYCU4DJQDaQBUwCJgITgExgPJABpANOYByQBowFxgCpQAowGnAAo4BkwA7YgCQgERgJWIERQAIQD8QBsYAFiAGigSggEjADJiACMALhgAEIA0IBPaADQgAtoJk1hKsaUAESwFirBJ90BjgN/Az8BJwC/gn8CPwD+AH4Hvg78DfgO+Bb4Bvga+Ar4CTwJfAF8DnwGfAp8FfgL8AnwMfAn4E/AR8BJ4A/Ah8CHwDvA+8B7wLvAH8A3gbeAt4E3gBeB14DXgVeAV4GXgJeBF4Afg88DzwHPAs8AzwNPAUcB34HPAk8ARwDHgceAx4FHgEeBo4CR4BB4DDwEPAgcAg4CASAAUAGHgDuB+4D7gX6gXuA3wJ3A3cBdwJ3ALcDtwG3ArcAB4CbgZuAG4EbgOuB64D9wLXANcDVwFXAlcAVwG+Ay4HLgEuBS4B9wMXARUAfcCGwF9gD7AZ2sdZZvdJOqB3AduACoBfYBmwFtgCbgU3ARmADsB5YB6wFeoA1wGqgG1gFdAErgRXAcqATWAYsBZYAHcBioB1oA1qBFqAZaAIagUXAQqABWADMB+oBP+AD5gFzAS9QB9QCc4BqoAqYDVQA5UAZUAqUAMWABygCCoEC1vpfvk3/tzfP/9/ewP/y9jH+WDb8YMYbG79oIf7wSXcTY2euOO8voKrZUraa9eK9m+1jV7DH2Xusme2A2s8OsDvZb5nMnmDPsbfPK/U/NM5s0C5n4erDLIRFMzZ0aujkmTuBQW3EOZ4rYEVr7Gc9Q+ahr37h++rMFUPmM4MhUSxMKWtUvYba/iadHjqFIzeEGYemcFu1B9qk3Olb3U1nHjhz13kdqGY1rJ7NZwtYA/4KrQn9b2UdbAkis4x1suVshWKtQNpi6HZYi5AL24uiz+ZaybrYStbN1rAethbvLujVQYunrVLsHrYO7/VsA9vINrHNbEvwuk7xbEbKRsW7Hilb2TaMzAVsu6IEk2cH28l2YdT2sL3sQozYr1sXDufqYxexizHOl7BL2a/pfeelXMYuY5ez32A+XMmuYlezazEvrmc3/MJ7jeK/jt3Ebsac4SWugudmRV3NrmGPsKfZg+x+9gB7SIllC2JLERFxaVci3YUYbEafd5zTYormuuFobUU0eL/7gv1ej/htP6fE2mAcefR2ICePTl9wHHgtW4IeEYnL0DPSZ/vJY8T7cOl5/RQl/n9e3mMepxsQLxEZHrOr4bvuX7zn5jhXX81uxAq8BVceVa5uhSZ1s6LP9d80nPeAknYbu53dgbG4i3ElmDx3wncXuxtr+x7Wz+7F+6w+V1Hq/ew+ZeRkNsAC7CA7hJF8iB1mg4r/P6U9gL3jl2UOBusKDNdyhB1lD2OGPMaOYad5Em/heRS+x4Pe40ousp9kv2PHlVw89UnMrWewQz3Pfs9eYC+zp2C9pFyfhfUKe429zt6WjFCvss9wPc1e0X7MItgs/PP/KEbjBrYQ7//Fl3YEs7ADQz8OrRv6UV3C2qU6PEDei1E6xC7GNxMrzt5asrEwzZ9YDDs09IN6AXjs6Xe1HWduHfraXb9715rV3au6Vq5Y3rls6ZKOxe1trc2LFjYsmF/v93nraufUVFfNrqwoLystKfYUFRbkz3K78mbOmD4tN2fqlMmZ4zPSx6amjHaMssXHRJpNRkNYqF4XotWo8XyeXuTwNNrl1EZZk+ooKcngtqMJjqZzHI2yHS7P+XlkOy/XhKTzcrqRs/0XOd2U0z2cUzLbZ7AZGen2IoddfrHQYR+U6mt80PsKHX67fFLRlYrWpCqGEUZyMkrYi+I7Cu2y1Ggvkj1rO/qKGgsz0qUBQ1iBo6AtLCOdDYQZIA1Q8lhH14A0Nk9ShGps0bQBFdMb+W1ldUpRU6tcXeMrKrQmJ/sVHytQ6pJDCmSdUpd9iYw2s4vsA+nH+i4eNLPmRmd4q6O1aYFPVjehUJ+6qK9vjxzplNMchXLaxo/jEcA2Od1RWCQ7HWhY+ZzhG0iyNsXssPd9z9B4x8kv0epzPE1BT0iK+XvGE3kXh8MkS01CM7QNLUT/kpN5Wy4adLNmGHJvjY9sO2u2Bpg70+mXVY085ZhIsXh5Sq9IGS7e6EBkixxFjcHP2o54ubfZnpGOkVU+KbImBel2WZ3a2NzSwbmprc9RiB4ilqzOJ7sLIdxNwWAWDUzIRP6mRnRiCQ9DjU/OdHTJMY58ijYcqCSlaEmtTylC3iI5pkBmjS3BUnJmEcpiihT18YHhDeR1OWp8R1jW0ImBbLv1YBbLZn7eDjm2AIOSWtTna22XbY3WVszPdrvPmiy7/Qif3+Fr8/NRcpjltBO4HV4YQKUU+vaL3CIzui3rUvR2n8qq9vPRgsPuwcWRPwMJZjmETD6i+TPsPsnKRDbcJZiDq/PqgaFOKShBYTCKFpRYkzG5ldd/aJKVOoBmyPrhNmnQCO3ZNtF9frVplJs3KM1e1FZ4TgPPqxSG0sBgbf++nSoei2Aw0AQ9H84S3oeMdBW0Hcl6WYV+Ki4+ivF2mVXbfY42h9+BOeSu9vHB4bFWxre81sG/XlVGOzhL6s6zKD2H0mSWXF7nEwb/5kn2OJVx5cOq2MWKPWyW/CK5VCRj32HVfX2tA0ydwqeydUBShLbgIr9c5fQ75GanI5m3MyN9QM/Ck+saC7B6Pdg5HZ4mh91s9/Q1DQ71NvcNuN19XUWNHdOwLvocpa19jlrfDAyushFssW7kbYli5VJ5XT6qUrH8AYe0t2bALe2trfcdMTNm31vnC6jwXXNjvn9gNNJ8R+yMuRWvinu5k2exc4PXNAeGXslvPeJmrFdJ1SgOxW4ZlJjio0zwSaxlUEU+s5JvIFW5kRv/d6JlUEMpblGDBj49+Xop99hgbj1SzDzlKMNBgi//0GZ60TeB7jCtW+8OdYerjCqElA9JAJ6jyBsqsYPhklGyDqBO9ABu/CQ9EOq2HlFqItdRqRc5ua8XtQezqRjPdk5FuCV13AsK9sBb7zsYzlC/ckWOfP7CFhLfgTmGg6bI3srn32Z/R1+jn+8eLBZzFR9Jlhx5TFY58tDikHA5zNGWLxsc+dzv4n4X+UO4X+fIl6VYCYM9iE23r9GBjRhryoefO/yY/ma+vFUp9sGhoTpf8ovWk/5krPkFQL1PDnXioNOmlCFfMUcj3MVyb0sTbwfzYi/jW09pix+LXVSILKVyKGoIDdaAHB6lDF9vKNSCuYYJqZTvhSH3+mW/k9/Ut4S3yG43y6zEMU0OSaU6tan8Rpn+vijHJL5ykVUOS9nDKRRtY7U+8lhh4mY4UXiPdOFoeYsDSS2NdkQdc6QWa5kOizA+D+Fpw56vSW1TEGYNJjLeLXWKwRgmh45HhfhwbRiPCvHR+REU3nnF2hPMgHubZQNalHpOKIMFEB0klfK24LMHjedZn+DV1AyyOY712Pt5o5Vb6ZAsG1NKm3C6UXkDPI4cURh16VO4i9dxnLw63vNwxB1bwuDQXY4NfIsTr4x0Bz/9+Pxj1iNYqMzf90uHPN+Zka7/pdeouPv69MZ/X4DipTcOM68FHWnhxxqYTzhlvtmL+AHrKBtQzUYOsKRwX5kDh5oqhQMPOmosn2R7q5/nQpOrlb3M8WuZUMVwJn5MK5X3mafzpxJuIV2xYODTJy8+3+wYNj1I9uBhMGU8oHxSMTB8319qlTsxM5GsZOEjYu+zmx3THPyCrqqxGoBGjNPwssD0x6zji6a3xe5rxmRHeDyNfZ4+3MTe0oRifA4G7ySvcJ5XJdaFhHWIgPAoyL3V9ka/vRGPplKNLznZitUItrc3yW5HEz8KqnF/fKpxJIGa+vgUZ37c1CrrcDC1N7U5knHgwOdX4qqMD+5Oy4ZZ+/ocfbKyEXiQGdWnYtmVcsKny+loauOP0LifvalNKetBc5Xo8PZZixxYy21oLY87+oX//cWa+aWlz4HaGhqdiERkX1SfPbcPW3ADTg9NasvcRhxV/ESyK0PdZIWFuJZyy4+KKGNoCs9IS4C3ZrlzoEGXctbD16K80kmZ9UqtaNkcn1wtCinrieda5ZRVcTlIREtlaQ52NsSf71MInjalFOF1Y+pZeWm7rMLxSsOjlC/lRbE10IBRMXiUQ0RZYjgkxWkjzqEFVsT0V/1ME8EYvq5n6jfZAk022w80qlaxFPVxNhlf8e8CuG+/Jgff+iAr3vwVju+IksDJLAz/aU/H9PCH4RdiFXayUKTx1+PscelyVYHqO/UezTXaRG1HiCHkHuRhZ1arX8M3ULxcLqtks9k18i6n7xGcP3NYLJsmPfigpbBQn6F7TCpAhXZ8v4zqpQK3SaMyHh4xwuU4PDlknzqydFDKOOTS7cMvJ67TH55+KfP0hyejcjNPSpkffPThR+ZvX4rMzcz66I2PJuKX9JgRxsOdKDrZcbhzsjpkX6c60sXLu0M7XW6Vbl8nKol3OUe85Hwp0/mSE9U4J0z0S5HJkQpiIlQ6XUyIY9R41eQxqVOysiblqSZnpzpGRagUX/aUqXnqrElJKjVykidPxW1J/drP9eqq0yGqrQ7X3Cxt0ghTjDFEqxoZH5UxI8VcOz9lxvhEnVoXotbqdWOn5o8q7ywa9a4uMtESmxil10clxloSI3Wn39NGnPpOG/FTgabzpyvVIdMXuEarrw3TqzQhIYNJ8QnjpieXzjVFmzWGaHNkrF4XFRk+tnDB6d2WkbyOkRYL1XW6EuFcMHRS7VI/z7Kw+f7A4+62m/Jt+Zn5akNoXHZ4uFSZbTbiEm/gymSWKrIHpX+4I9iYMSYmhTOzSapk0waHvsEDSSX404PIrTAKcD7Ey0wbVOndMZFxT7Fsc7Zq+rFsiWVL2dnjZ40blKxu0yujpFGjNImfjy+b+X54pYZluk66+Ng1nIzk11ULGzCKH/HvYo87FzbkZpoVPSl34oSFDVa30RAnZcc91cnrG6VUGNvJRkmxGtQ5PvHzzvFl4TPf7+T1xme6nC4+mvjHv5NX7WzAsKbEhGAoU1MnT+aMIc3GYGVNzh6PoRsePg0fPouOeywxsVmTpkxVu8wjrSNsEdMvryleXZORt+buJZtjJ87OndlUOjFcHx6q0Vnz57ZnN+2tS719X2Frvs1fPWvlzPjw8JCQ8PB6lyfF0z6roqssxZNdPdma6EjUmxNMCYkjHInR6d6tdcfjMlxpntr8QozRfozRm9pVbBybyR7iY/SgyyWFJU8ZHPqBRx38DY86+NODiDq3lahPGZR+dFstzihkctqRw8lH0RlvxoWPm3NQFeYOZZawKZOTNdoJg5L2odQyq8dckQs5oK1kGAYXBiIuV8p8Qwm582zkG6yHqVwqL4iFQ0W1vGygE4WxflDaidJ86QQXwhiLWBGRPMo8ppG0VHTBeOsiYxHePJX6zayWyxqcpR7PGH2U1RIzMipEF22PT7BH6ceWl5SMbb5o3tj7Ldlz3fY8d9GYws0Feb6pCdJfex7e6YlMnZa2Qh+u02h04Xptjj5cr9HgcvqTtByHefYOuadoe+vMqHH5k87sr503o2UTQ4wbEeMb8M16KvagR5R1YHNNlwzWXD77c8MQwVwz4pbL45bLw5j7MH5+ZSxz6ASPfSaPPdLB3xxEFoVRSPEjdyYPdVh0sseQO8aqicAs1Qbiy7CUNAcjKrUVDGFWAu0KznNnMN58jmOKh4mC8bzkoc74sghe9lCnUjgeYVYC7VJmc3DOnjubJ8XGDcdbnapsVJaYJMQ/TzVVfYMucmQM3xuK989vuXje2EnNly+q2uHWxdh4tEPvLNhS6EJsEetZyTPdnjEJIrTrKudW7hhoXvPwzuKiApVBZ9RptbicLkJUmze7C7e3IcoFE3EWpOA3hd3a9WwG28tjG4g1s8GhE4cQGGYNzl7OyqyFUKY1WJnO1kHpn4EJ41IGh15xR5kjpYqUsJNTikeknpxQYq8wlyB0rpOTXNggnMezvqU9Ius43+Qjp4Sd7ETOCaknO4N5ESmXc5JL2cyDE1FZ0RZlJmJHd0QOz09sCWInUGaqRrVbo9WH6CxJadaUbHvEc3pDqDbK9JweszLeHq3fZjbzWbbNUbK8zJE/Olyv1pqi4yK0oYbQ+Kyaac26yBHRo+0/f6E38Olo0Kst9tHRIyJ1DQv3zE0zmsKjrTh22eQzV6gvVD/L8nAOLpJilZloicoo5vOuWI8ZVWw3R0sVxVmuwaEf+eoHKzMOfOIhnuTSVUG6jaYoqaLKqjFNUGfpdPDw6YmYHnMbITKydFarLitDw8fBnc0Hwsdv4bObUcw3LsVtAKeYJujUOWXvhtd+arE05qg/m1Eyzp7/Tk7Z/HfsVXx3wCbtUvbok2/RNuHMepFv0nG5mXg5scfG5ZpfdOLjFBc+MmOUesPL3u0Mt1hqP+3klc9Qf9bJq8/Jf6czp8w+/51O3ILvIdivXbRZm58e3k0wUrGxtJekjgkJwZYcF5ekHt5csLdMxYaOc5hf+fDFxiVPipXojFY28DxVdHbqmDERKET7zYXRpgscIyc19M6e2mKNips15YuCrjnjs5fduWr5/uZ0c/JE+8TMSSm20dkLLqhIK7ZJ5sjIM2faGiYUZ8a1zZ9YkhlXu6jmM3tafOjOteVteVb1Godt9LzM2etr0xNjo8YnOcarwlTJM/3T87q8E1Pc/uzkvJyshISK9JmNqSkN+ZUb6zJC9clnvl2w2J5TOtbfbptacnrhNJdKn5CRNtYyqyBxQh7fp3YNnZJqtJn4VjyZ3cVnx2GXo8qx0qGO5csFOxFYWT6KHa3YytDDVuaJ4sd4xz6M57qRzEJnB/4wUCkFVhYdmFajBWfIQ2E2N+YG/iA471CCuVTZrt466QxuVcGdysk3qoEEnunBTsqF1fY0X2opw2tK7PnR/OGJjw8GRsrTR9kTsIJ0Yn+PTp8+zcmRMLxWdmIn55u4TpowbVxaLoBdZT9ikYdz0cJqKBJxVXEr49SY0sp5CFb6BFb6xP3KDsPQp0NhZo/SkWAveOsPKi60+t+2+V/bOdy8s2cMtUp9AE9Uk9igsnpNUWaEO5pfXNnSuGjeOqxAhRFVhTFuYKW1YKW10crhnWTgp46BH0IGvj4N/CQy8PPHgPTDzA2TJZkHpRB3WEbZuITRpQkVSrdcfGVKWIPnnttOfprg7xOtAxlKEUPnOWX43siX2vmd5w9BIbqzu6IYQcsUjB1Ob/UBjJ5yKsePL52Qt7lQDGZI1Mi42ESzruKayvpNFcnDsVKZKhcWjvZ5T190dnCxk6rVoQb9Om/VzPYLG/k8r8d5/CGiGM3GsOeUOI50pUljo6S0SCnVKKWGS6l6KVUnjVNLaSopiQcNgQIrkxysHMvgr/gmqaQjaEn8GE7KDJPCYvhTUAwPaQx/Morhz0gxPK4xR/HHWWzo2GETq+zCcCYMSlLAVOYYlFTBRyKEtSEY1swGCi/CKl7WARMvcqjTVKblhYafhc49dGhPSlKJhx5xGKs/nLb6vu6Vd6yYkrv63tXgqfdb85ZWlS4pTLa6llaVLC20S5+sOLK7PH/roW5wGXhz6fbm3OxF2yvLtjflZi/czv+tJbEogL9C8Gsdm8VfBc6Cps4lzd1L/h8KZ+RgCmVuZHN0cmVhbQplbmRvYmoKMTMgMCBvYmoKPDwgL1RpdGxlIChNaWNyb3NvZnQgV29yZCAtIERvY3VtZW50MSkgL1Byb2R1Y2VyIChtYWNPUyBWZXJzaW9uIDEyLjMgXChCdWlsZCAyMUUyMzBcKSBRdWFydHogUERGQ29udGV4dCkKL0NyZWF0b3IgKFdvcmQpIC9DcmVhdGlvbkRhdGUgKEQ6MjAyMjEwMTgwODA4MTlaMDAnMDAnKSAvTW9kRGF0ZSAoRDoyMDIyMTAxODA4MDgxOVowMCcwMCcpCj4+CmVuZG9iagp4cmVmCjAgMTQKMDAwMDAwMDAwMCA2NTUzNSBmIAowMDAwMDAwMjU0IDAwMDAwIG4gCjAwMDAwMDMyMDIgMDAwMDAgbiAKMDAwMDAwMDAyMiAwMDAwMCBuIAowMDAwMDAwMzU4IDAwMDAwIG4gCjAwMDAwMDMxNjcgMDAwMDAgbiAKMDAwMDAwMDAwMCAwMDAwMCBuIAowMDAwMDAzMzM0IDAwMDAwIG4gCjAwMDAwMDA0NTUgMDAwMDAgbiAKMDAwMDAwMzI4NSAwMDAwMCBuIAowMDAwMDAzODg5IDAwMDAwIG4gCjAwMDAwMDM1MzIgMDAwMDAgbiAKMDAwMDAwNDEyNSAwMDAwMCBuIAowMDAwMDEzMTk3IDAwMDAwIG4gCnRyYWlsZXIKPDwgL1NpemUgMTQgL1Jvb3QgOSAwIFIgL0luZm8gMTMgMCBSIC9JRCBbIDw4NzhiM2Q3NzYwZmI0YmE1YzA2YzY1ZjQ4NDU5NTM2ND4KPDg3OGIzZDc3NjBmYjRiYTVjMDZjNjVmNDg0NTk1MzY0PiBdID4+CnN0YXJ0eHJlZgoxMzQxMgolJUVPRgo=",
        attDtCreate: "2022-09-20 15:30:00"
    },
    {
        attId: 'DOC124', attSeq: '1',
        attType: 'BILL OF LADING',
        attReferenceid: 'CKDOJ2022081063438',
        attName: 'CKDOJ2022081063438_BL.pdf',
        attDesc: 'Bill of Lading CKDOJ2022081063438',
        attData: "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",
        attDtCreate: "2022-09-21 16:38:00"
    },
    {
        attId: 'DOC125', attSeq: '1',
        attType: 'BILL OF LADING',
        attReferenceid: 'CKDOJ2022081073452',
        attName: 'CKDOJ2022081073452_BL.pdf',
        attDesc: 'Bill of Lading CKDOJ2022081073452',
        attData: "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",
        attDtCreate: "2022-09-22 15:30:00"
    },
];

Mock.onGet("/api/jobs/submitted").reply((config) => {
    let count = { i: 0 };
    jobsDb.list.forEach((element) => {
        if (element.status === MOCK_JOBS_STATUS.SUBMITTED.name) {
            count.i++;
        }
    });
    return [200, count];
});

Mock.onGet("/api/jobs/active").reply((config) => {
    let count = { i: 0 };
    doClaimJobsDb.list.forEach((element) => {
        if (element.status === MOCK_JOBS_STATUS.IN_PROGRESS.code
            || element.status === MOCK_JOBS_STATUS.SUBMITTED.code) {
            count.i++;
        }
    });
    return [200, count];
});
