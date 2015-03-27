import csv
import inflection
import tempfile
import subprocess

TARGET_SCALE = 0.5

SPRITE_BASE = 'core/assets/'

def sprite(path, frame=0):
    return SPRITE_BASE + path.replace('%', '{f:05}'.format(f=frame))

with open('sprites.txt', 'r') as csvfile:
    reader = csv.DictReader(csvfile)
    for row in reader:
        source_path = row['path']
        base = ''
        elements = source_path.split('/')
        if elements[0] and len(elements) > 1:
            base = elements[0].upper() + '_'
        real_name = base + row['key']
        frames = int(row['frames'])
        source_scale = float(row['scale'])
        output_scale = max(TARGET_SCALE, source_scale)
        process_scale = source_scale / output_scale
        if frames > 1:
            target_path = inflection.dasherize(real_name).lower() + '-%.png'
        else:
            target_path = inflection.dasherize(real_name).lower() + '.png'
        with tempfile.NamedTemporaryFile(suffix='.png') as tf:
            tf_name = tf.name
            for frm in range(frames):
                src_file = sprite(source_path, frm)
                dst_file = sprite(target_path, frm)
                command = ('convert', src_file, '-resize', '{0:.3f}%'.format(process_scale * 100), tf_name)
                subprocess.check_call(command)
                command = ('pngcrush', '-new', tf_name, dst_file)
                subprocess.check_call(command)
                command = ('git', 'rm', src_file)
                subprocess.check_call(command)
                command = ('git', 'add', dst_file)
                subprocess.check_call(command)
        print("{key}({path!r}, {frames}, {oscale}f, {ax}, {ay})".format(
              key=row['key'],
              path=target_path,
              oscale=output_scale,
              frames=row['frames'],
              ax=int(int(row['ax'])*process_scale),
              ay=int(int(row['ay'])*process_scale)))
