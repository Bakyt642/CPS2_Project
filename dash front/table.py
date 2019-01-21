import dash
from dash.dependencies import Input, Output, State
import dash_core_components as dcc
import dash_html_components as html
import dash_table_experiments as dt
import json
import pandas as pd
import numpy as np
import plotly

import urllib.request
import json
import pandas as pd



app = dash.Dash()

app.scripts.config.serve_locally = True
# app.css.config.serve_locally = True

url = urllib.request.urlopen('http://172.20.10.9:8080/api/itm/enviroment?all') #opening url
data = url.read().decode()#str
js = json.loads(data) #list
jsonDict = json.loads(data)
jsonDict #dict
type(jsonDict)
s = pd.Series(jsonDict)
dfq = pd.DataFrame(jsonDict)
dfq.to_csv('morad.csv', sep='\t', encoding='utf-8')

DF_GAPMINDER = pd.read_csv(
    'morad.csv', sep = '\t'
)





app.layout = html.Div([
    html.H4('ITM factory  DataTable'),
    dt.DataTable(
        rows=DF_GAPMINDER.to_dict('records'),

        # optional - sets the order of columns
        columns=sorted(DF_GAPMINDER.columns),

        row_selectable=True,
        filterable=True,
        sortable=True,
        selected_row_indices=[],
        id='datatable-gapminder'
    ),
    html.Div(id='selected-indexes'),
    dcc.Graph(
        id='graph-gapminder'
    ),
], className="container")


@app.callback(
    Output('datatable-gapminder', 'selected_row_indices'),
    [Input('graph-gapminder', 'clickData')],
    [State('datatable-gapminder', 'selected_row_indices')])
def update_selected_row_indices(clickData, selected_row_indices):
    if clickData:
        for point in clickData['points']:
            if point['pointNumber'] in selected_row_indices:
                selected_row_indices.remove(point['pointNumber'])
            else:
                selected_row_indices.append(point['pointNumber'])
    return selected_row_indices


@app.callback(
    Output('graph-gapminder', 'figure'),
    [Input('datatable-gapminder', 'rows'),
     Input('datatable-gapminder', 'selected_row_indices')])
def update_figure(rows, selected_row_indices):
    dff = pd.DataFrame(rows)
    fig = plotly.tools.make_subplots(
        rows=3, cols=1,
        subplot_titles=('Box graph', 'Bar graph', 'Scatter plot',),
        shared_xaxes=True)
    marker = {'color': ['#0074D9']*len(dff)}
    for i in (selected_row_indices or []):
        marker['color'][i] = '#FF851B'
    fig.append_trace({
        'x': dff['sensorName'],
        'y': dff['value'],
        'type': 'bar',
        'marker': marker
    }, 1, 1)
    fig.append_trace({
        'x': dff['sensorName'],
        'y': dff['value'],
        'type': 'box',
        'marker': marker
    }, 2, 1)
    fig.append_trace({
        'x': dff['sensorName'],
        'y': dff['value'],
        'type': 'scatter',
        'marker': marker
    }, 3, 1)
    fig['layout']['showlegend'] = True
    fig['layout']['height'] = 800
    fig['layout']['margin'] = {
        'l': 40,
        'r': 10,
        't': 60,
        'b': 200
    }
    fig['layout']['yaxis3']['type'] = 'log'
    return fig


app.css.append_css({
    'external_url': 'https://codepen.io/chriddyp/pen/bWLwgP.css'
})

if __name__ == '__main__':
    app.run_server(debug=True)
