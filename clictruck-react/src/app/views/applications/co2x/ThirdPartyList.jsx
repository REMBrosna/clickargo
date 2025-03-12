import React from "react";
import useHttp from "app/c1hooks/http";
import { useEffect, useState } from "react";
import { Box, Button, IconButton, Icon } from "@material-ui/core";
import clsx from "clsx";
import LinkIcon from "@material-ui/icons/Link";
import { useHistory } from "react-router-dom/cjs/react-router-dom";
import { MatxLoading } from "matx";
import { isEmpty } from "app/c1utils/utility";

export default function CO2xRedirect() {
  const { isLoading, res, error, urlId, sendRequest } = useHttp();
  const history = useHistory();

  const [loading, setLoading] = useState(true);
  const [partyList, setPartyList] = useState([]);

  //check if the principal account exist in co2x
  useEffect(() => {
    sendRequest("/api/v1/clickargo/thirdparty/list/-", "list3Party", "get");
  }, []);

  useEffect(() => {
    if (!isLoading && !error && res) {
      switch (urlId) {
        case "list3Party": {
          setLoading(false);
          if (res?.data) {
            setPartyList(res?.data);
          }
          break;
        }
        default:
          break;
      }
      setLoading(false);
    }
    if (error) setLoading(false);
    // eslint-disable-next-line
  }, [urlId, res, isLoading, error]);

  const handleClick = (party) => {
    let form = document.createElement("form");
    form.method = party.thirdParty?.method;
    form.action = party.url;
    form.target = "_blank"; 

    let parameters = party.parameters;
    for (let key in parameters) {
        if (parameters.hasOwnProperty(key)) {
            let input = document.createElement("input");
            input.type = "hidden";
            input.name = key;
            input.value = parameters[key];
            form.appendChild(input);
        }
    }

    console.log("form", form);

    document.body.appendChild(form);
    form.submit();
    document.body.removeChild(form); 
  }

  return loading ? (
    <MatxLoading />
  ) : (
    <>
      <br />
      {partyList.map((party, idx) => {
        return (
          <ul key={idx}>
            <li>
              {" "}
              <b>{party.thirdParty?.label} </b>
              {false && (
                <IconButton color="primary" aria-label="add to shopping cart">
                  <LinkIcon />
                </IconButton>
              )}
              {(party?.parameters && !isEmpty(party?.parameters)) && <Button
                variant="contained"
                color="primary"
                startIcon={<Icon>link</Icon>}
                style={{ borderRadius: "20px", marginLeft: "30px" }}
                onClick={() => handleClick(party)}
              >
                Link
              </Button> }
            </li>
            <div>{party?.thirdParty?.desc}</div>
          </ul>
        );
      })}
    </>
  );
}
