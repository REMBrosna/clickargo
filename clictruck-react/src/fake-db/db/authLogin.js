import Mock from "../mock";
import jwt from "jsonwebtoken";

const JWT_SECRET = "jwt_secret_key";
const JWT_VALIDITY = "7 days";

const userList = [
  {
    password: "",
    username: "TO_DERIAN",
    authorities: [
      {
        authority: "TO_OPERATION",
      },
    ],
    accountNonExpired: true,
    accountNonLocked: true,
    credentialsNonExpired: true,
    enabled: true,
    vccSessionId: "04C522F73EC28E784D9F97F31B6C6178",
    exp: 1677134858865,
    groupList: "",
    coreAccn: {
      otherLangDesc: null,
      coreMstLocale: null,
      accnId: "ALMGA",
      accnStatus: "A",
      accnName: "PT Alamboga",
      accnNameOth: "PT Alamboga",
      accnCoyRegn: "21",
      accnPassNid: null,
      accnNationality: "Indonesia",
      accnVatFlag: null,
      accnVatNo: null,
      accnAddr: {
        otherLangDesc: null,
        coreMstLocale: null,
        addrLn1: "Jl. Sunia Negara No.33",
        addrLn2: "Pemogan, Denpasar Selatan",
        addrLn3: "Bali 80221",
        addrPcode: "80221",
        addrCity: "Bali",
        addrProv: "Bali",
        addrCtry: null,
      },
      accnContact: {
        otherLangDesc: null,
        coreMstLocale: null,
        contactTel: "6281389000500",
        contactFax: "6281389000500",
        contactEmail: "info@alamboga.com",
      },
      accnDtAgree: null,
      accnDtReg: 1664553600000,
      accnDtDereg: null,
      accnDtRereg: null,
      accnDtSusp: null,
      accnDtReins: null,
      accnDtCreate: 1664553600000,
      accnUidCreate: "SYS",
      accnDtLupd: 1664553600000,
      accnUidLupd: "SYS",
      accnBusinessAct: null,
      accnOwnerNationality: null,
      accnOwnerName: null,
      cityCode: "20",
      coreLocale: null,
      TMstAccnType: {
        otherLangDesc: null,
        coreMstLocale: null,
        atypId: "ACC_TYPE_TO",
        atypDescription: "TRUCK OPERATOR",
        atypDescriptionOth: null,
        atypStatus: "A",
        atypDtCreate: 1664553600000,
        atypUidCreate: "SYS",
        atypDtLupd: 1664553600000,
        atypUidLupd: "SYS",
      },
    },
    name: "DERIAN PRATAMA",
    id: "TO_DERIAN_001",
    role: "TO_OPERATION",
  },
];

Mock.onPost("/api/v1/clickargo/clictruck/auth/login").reply(async (config) => {
  try {
    await new Promise((resolve) => setTimeout(resolve, 1000));

    const { id } = JSON.parse(config.data);
    const user = userList.find((u) => u.username === id);

    if (!user) {
      return [400, { message: "Invalid username or password" }];
    }

    const token = jwt.sign({ userId: user.id }, JWT_SECRET, {
      expiresIn: JWT_VALIDITY,
    });

    return [
      200,
      {
        token,
        loginStatus: "AUTHORIZED_LOGIN",
        user,
        err: null
      },
    ];
  } catch (err) {
    console.error(err);
    return [500, { message: "Internal server error" }];
  }
});
