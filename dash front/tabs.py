import pandas as pd
import dash
from dash.dependencies import Input, Output
import dash_core_components as dcc
import dash_html_components as html
from loremipsum import get_sentences

import urllib.request
import json
import pandas as pd

url = urllib.request.urlopen('http://172.20.10.9:8080/api/itm/enviroment?all') #opening url
data = url.read().decode()#str
js = json.loads(data) #list
jsonDict = json.loads(data)
jsonDict #dict
type(jsonDict)
s = pd.Series(jsonDict)
df1 = pd.DataFrame(jsonDict)
df1.to_csv('morad.csv', sep='\t', encoding='utf-8')

df = pd.read_csv('morad.csv', sep='\t')
df.set_index('sensorName', inplace=True)
df['time'] = pd.to_timedelta(df['time'].str.strip())






app = dash.Dash()

app.scripts.config.serve_locally = True

app.layout = html.Div([
    dcc.Tabs(
        tabs=[
            {'label': 'Tab {}'.format(i), 'value': i} for i in range(1, 4)
        ],
        value=3,
        id='tabs'
    ),
    html.Div(id='tab-output')
], style={
    'width': '80%',
    'fontFamily': 'Sans-Serif',
    'margin-left': 'auto',
    'margin-right': 'auto'
})


@app.callback(Output('tab-output', 'children'), [Input('tabs', 'value')])
def display_content(value):
    lstTempVal = []
    for i in df.loc['temp', 'value']:
        lstTempVal.append(i)
    lstTempT  = []
    for i in df.loc['temp', 'time']:
        lstTempT.append(i)


    lstHumVal = []
    for i in df.loc['hum', 'value']:
        lstHumVal.append(i)
    lstHumT  = []
    for i in df.loc['hum', 'time']:
        lstHumT.append(i)

    lstLumVal = []
    for i in df.loc['lum', 'value']:
        lstLumVal.append(i)
    lstLumT  = []
    for i in df.loc['lum', 'time']:
        lstLumT.append(i)

    data = [
        {
            'x': lstTempT,
            'y': lstTempVal,
            'name': 'Temperature',
            'marker': {
                'color': 'rgb(200, 56, 50)'
            },
            'type': ['bar', 'scatter', 'box'][int(value) % 3]
        },
        {
            'x': lstHumT,
            'y': lstHumVal,
            'name': 'Humidity',
            'marker': {
                'color': 'rgb(200, 118, 255)'
            },
            'type': ['bar', 'scatter', 'box'][int(value) % 3]
        },
        {
            'x': lstLumT,
            'y': lstLumVal,
            'name': 'Lum',
            'marker': {
                'color': 'rgb(87, 118, 255)'
            },
            'type': ['bar', 'scatter', 'box'][int(value) % 3]
        }

    ]

    return html.Div([
        dcc.Graph(
            id='graph',
            figure={
                'data': data,
                'layout': {
                    'margin': {
                        'l': 30,
                        'r': 0,
                        'b': 100,
                        't': 0
                    },
                    'legend': {'x': 0, 'y': 1}
                }
            }
        ),

    ])


if __name__ == '__main__':
    app.run_server(debug=True)
