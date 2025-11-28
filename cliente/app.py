import os
import time
import streamlit as st
import requests
from concurrent.futures import ThreadPoolExecutor, as_completed
from typing import Tuple, Any

USD_URL = os.getenv("USD_URL", "http://usd-service:8081/convert/usd")
EUR_URL = os.getenv("EUR_URL", "http://eur-service:8082/convert/eur")

st.set_page_config(page_title="Cliente Conversor (threads)", layout="centered")
st.title("Cliente - Chamadas concorrentes: Dólar e Euro para Real")

amount = st.text_input("Valor", "1")

col1, col2 = st.columns(2)
with col1:
    usd_ph = st.empty()
with col2:
    eur_ph = st.empty()

def format_converted(value: Any) -> str:
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

def call_once(url: str, amount: str, timeout: float = 10.0) -> Tuple[bool, Any]:
    try:
        resp = requests.get(url, params={"amount": amount}, timeout=timeout)
        resp.raise_for_status()
        return True, resp.json()
    except requests.exceptions.Timeout:
        raise TimeoutError("A requisição excedeu o tempo limite.")
    except Exception as e:
        return False, f"Erro na requisição: {e}"

def worker(name: str, url: str, amount: str, timeout: float = 10.0):
    ok, payload = call_once(url, amount, timeout=timeout)
    return name, ok, payload

if st.button("Converter (USD e EUR)"):
    usd_ph.info("Chamando USD → BRL...")
    eur_ph.info("Chamando EUR → BRL...")

    with ThreadPoolExecutor(max_workers=2) as executor:
        futuro_dict = {
            executor.submit(worker, "USD", USD_URL, amount): "USD",
            executor.submit(worker, "EUR", EUR_URL, amount): "EUR",
        }

        for futuro in as_completed(futuro_dict):
            nome_esperado = futuro_dict[futuro]
            try:
                nome_servico, ok, carga = futuro.result()
            except Exception as exc:
                ph = usd_ph if nome_esperado == "USD" else eur_ph
                ph.error(f"{nome_esperado} — erro inesperado: {exc}")
                continue

            ph = usd_ph if nome_servico == "USD" else eur_ph

            if not ok:
                ph.error(f"Erro {nome_servico}: {carga}")
            else:
                convertido = carga.get("converted") if isinstance(carga, dict) else None
                ph.success(format_converted(convertido))
