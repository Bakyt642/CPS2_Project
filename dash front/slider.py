import pandas as pd
import dash
import dash_html_components as html
import dash_core_components as dcc
import plotly.graph_objs as go
from textwrap import dedent

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


urlw = urllib.request.urlopen('http://172.20.10.9:8080/api/itm/worker?all') #opening url
dataw = urlw.read().decode()#str
jsw = json.loads(dataw) #list
jsonDictw = json.loads(dataw)
jsonDictw #dict
type(jsonDictw)
sw = pd.Series(jsonDictw)
dfw = pd.DataFrame(jsonDictw)
dfw.to_csv('moradw.csv', sep='\t', encoding='utf-8')
dfw





df = pd.read_csv("morad.csv", sep = '\t')
fl = 'moradw.csv'
fd = pd.read_csv(fl, sep = '\t')

# Utility functions
def generate_table(dataframe, max_rows=10):
    return html.Table(
        # Header
        [html.Tr([html.Th(col) for col in dataframe.columns])] +

        # Body
        [html.Tr([
            html.Td(dataframe.iloc[i][col]) for col in dataframe.columns
        ]) for i in range(min(len(dataframe), max_rows))]
    )

# Dash app
app = dash.Dash()
app.css.append_css({
    'external_url': 'https://codepen.io/chriddyp/pen/bWLwgP.css'
})

app.layout = html.Div([
    html.H1('ITM factory',
    ),

    dcc.Markdown(dedent('''
    Latest five data
    ''')
    ),

    html.H2("Latest data | 21.01.2019"),
    dcc.Markdown(dedent('''

    ''')
    ),
    dcc.Markdown(dedent('''

    ''')
    ),

    generate_table(df.tail(5)),
    generate_table(fd.head()),


    dcc.Dropdown(
        id='country-dropdown',
        options=[{'label': i, 'value': i} for i in df.sensorName.unique()],
        multi=True,
        value=['temp']
    ),
    dcc.Graph(id='timeseries-graph'),



])

@app.callback(
    dash.dependencies.Output('timeseries-graph', 'figure'),
    [dash.dependencies.Input('country-dropdown', 'value')])
def update_graph(country_values):
    dff = df.loc[df['sensorName'].isin(country_values)]

    return {
        'data': [go.Scatter(
            x=dff[dff['sensorName'] == sensorName]['time'],
            y=dff[dff['sensorName'] == sensorName]['value'],

            mode='lines+markers',
            name=sensorName,
            marker={
                'size': 25,
                'opacity': 0.5,
                'line': {'width': 0.5, 'color': 'white'}
            }
        ) for sensorName in dff.sensorName.unique()],
        'layout': go.Layout(
            title="Sensors",
            xaxis={'title': 'time'},
            yaxis={'title': 'value'},
            margin={'l': 60, 'b': 100, 't': 80, 'r': 0},
            hovermode='closest'
        )
    }



if __name__ == '__main__':
    app.run_server(debug=True)
