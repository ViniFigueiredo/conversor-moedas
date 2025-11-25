import streamlit as st
import requests
from concurrent.futures import ThreadPoolExecutor, as_completed

#url do springboot
BASE_URL = "http://api:8080/convert"

st.set_page_config(page_title="Cliente Conversor", layout="centered")
st.title("Cliente — duas threads: Dólar para Real e Euro para Real")

amount = st.text_input("Valor (ex.: 1.234,56 ou 1234.56)", "1.000,00")

col1, col2 = st.columns(2)
with col1:
    usd_ph = st.empty()
with col2:
    eur_ph = st.empty()

if st.button("Converter simultaneamente (USD e EUR)"):
    usd_ph.info("Aguardando USD para BRL...")
    eur_ph.info("Aguardando EUR para BRL...")
    results = {}

    with ThreadPoolExecutor(max_workers=2) as ex:
        futures = {
            ex.submit(requests.get, f"{BASE_URL}/usd", {"amount": amount}): "usd",
            ex.submit(requests.get, f"{BASE_URL}/eur", {"amount": amount}): "eur"
        }

        for future in as_completed(futures):
            key = futures[future]
            try:
                resp = future.result(timeout=15)
                resp.raise_for_status()
                data = resp.json()
                results[key] = data

                # pega SÓ O CONVERTED
                converted = data.get("converted")

                if key == "usd":
                    usd_ph.success(f"{converted}")
                else:
                    eur_ph.success(f"{converted}")

            except Exception as e:
                if key == "usd":
                    usd_ph.error(f"Erro USD: {e}")
                else:
                    eur_ph.error(f"Erro EUR: {e}")

    st.markdown("Json retorno da API")
    st.json(results)
