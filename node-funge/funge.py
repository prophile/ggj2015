from lxml import etree
import sys

SVG = '{http://www.w3.org/2000/svg}'

def svg(x):
    return SVG + x

WIDTH = 2000
HEIGHT = 1500

root = etree.Element(svg('svg'), width=str(WIDTH), height=str(HEIGHT),
                     viewBox='{} {} {} {}'.format(-WIDTH/2,
                                                  -HEIGHT/2,
                                                  WIDTH,
                                                  HEIGHT))

container = root

NODES = {}

def add_node(x, y, name, text=None):
    node = etree.Element(svg('circle'),
                         cx=str(x),
                         cy=str(-y),
                         r=str(8),
                         fill='red')
    container.append(node)
    if text is not None:
        text_node = etree.Element(svg('text'),
                                  x=str(x),
                                  y=str(-y + 20),
                                  style='text-anchor: middle;')
        text_node.text = text
        container.append(text_node)
    NODES[name] = (x, y)

def add_connection(src, dst):
    x1, y1 = NODES[src]
    x2, y2 = NODES[dst]
    node = etree.Element(svg('line'),
                         x1=str(x1),
                         y1=str(-y1),
                         x2=str(x2),
                         y2=str(-y2),
                         stroke='blue')
    container.append(node)

labels = {'WAYPOINT': None,
          'SPAWNER': None,
          'BASE': 'HOME',
          'WELL': 'well',
          'MINE': 'mine',
          'WRECKAGE': 'wreckage'}

with open(sys.argv[1]) as f:
    for line in f:
        elements = line.strip().split(' ')
        if not elements:
            continue
        if elements[0] == 'node':
            typ, name, x, y, r = elements[1:]
            add_node(int(x), int(y), name, labels[typ])
        elif elements[0] == 'conn':
            add_connection(elements[1], elements[2])

bees = etree.tostring(root, pretty_print=True, encoding='utf-8', xml_declaration=True)

with open(sys.argv[2], 'w') as f:
    f.write(bees)

