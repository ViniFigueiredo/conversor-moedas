# cliente/app.py
import os
import streamlit as st
import requests

USD_URL = os.getenv("USD_URL", "http://usd-service:8081/convert/usd")
EUR_URL = os.getenv("EUR_URL", "http://eur-service:8082/convert/eur")

st.set_page_config(page_title="Cliente Conversor", layout="centered")
st.title("Cliente - duas threads: Dólar e Euro para Real")

amount = st.text_input("Valor", "1")

col1, col2 = st.columns(2)
with col1:
    usd_ph = st.empty()
with col2:
    eur_ph = st.empty()

def format_converted(value):
    if value is None:
        return "null"

    s = str(value).strip()

    if "." in s:
        inteiro, decimal = s.split(".", 1)
    else:
        inteiro, decimal = s, "00"

    if len(decimal) == 1:
        decimal = decimal + "0"
    elif len(decimal) > 2:
        decimal = decimal[:2]

    inteiro_rev = inteiro[::-1]
    grupos = [inteiro_rev[i:i+3] for i in range(0, len(inteiro_rev), 3)]
    inteiro_brl = ".".join(grupos)[::-1]

    return f"{inteiro_brl},{decimal}"


def call_convert(url: str, amount: str, timeout: float = 10.0):
    try:
        resp = requests.get(url, params={"amount": amount}, timeout=timeout)
    except requests.exceptions.RequestException as e:
        return False, f"Request failed: {e}"

    try:
        resp.raise_for_status()
    except requests.exceptions.HTTPError:
        try:
            j = resp.json()
            msg = j.get("message") or j
        except Exception:
            msg = resp.text
        return False, f"HTTP {resp.status_code}: {msg}"

    try:
        data = resp.json()
    except ValueError:
        return False, "Invalid JSON response"

    return True, data

if st.button("Converter (USD e EUR)"):
    usd_ph.info("Chamando USD → BRL...")
    eur_ph.info("Chamando EUR → BRL...")

    results = {}

    ok_usd, payload_usd = call_convert(USD_URL, amount, 10.0)
    if not ok_usd:
        usd_ph.error(f"Erro USD: {payload_usd}")
        results["usd"] = {"error": payload_usd}
    else:
        results["usd"] = payload_usd
        converted = payload_usd.get("converted") if isinstance(payload_usd, dict) else None
        usd_ph.success(format_converted(converted))

    ok_eur, payload_eur = call_convert(EUR_URL, amount, 10.0)
    if not ok_eur:
        eur_ph.error(f"Erro EUR: {payload_eur}")
        results["eur"] = {"error": payload_eur}
    else:
        results["eur"] = payload_eur
        converted = payload_eur.get("converted") if isinstance(payload_eur, dict) else None
        eur_ph.success(format_converted(converted))
